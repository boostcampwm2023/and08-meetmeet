import {
  BadRequestException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { v4 as uuidv4 } from 'uuid';
import { compare } from 'bcrypt';
import { UserService } from 'src/user/user.service';
import { AuthUserDto } from './dto/auth-user.dto';
import { User } from 'src/user/entities/user.entity';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class AuthService {
  constructor(
    private readonly userService: UserService,
    private readonly jwtService: JwtService,
    private readonly configService: ConfigService,
  ) {}

  async register(authUserDto: AuthUserDto) {
    const { email, password } = authUserDto;

    const existedUser = await this.userService.findUserByEmail(email);
    if (existedUser) {
      throw new BadRequestException('중복된 이메일입니다.');
    }

    const nickname = uuidv4().split('-').at(0)!;
    // TODO: nickname 중복 확인

    await this.userService.localCreateUser(email, password, nickname);
  }

  async validateUser(email: string, password: string) {
    const user = await this.userService.findUserWithPasswordByEmail(email);

    if (!user || !(await compare(password, user.password))) {
      throw new BadRequestException();
    }

    return user;
  }

  async kakaoLogin(kakaoId: string) {
    const user = await this.userService.findUserByOAuth(kakaoId, 'kakao');

    if (user) {
      return this.login(user);
    }

    const nickname = uuidv4().split('-').at(0)!;
    const createdUser = await this.userService.oauthCreateUser(
      kakaoId,
      nickname,
      'kakao',
    );

    return this.login(createdUser);
  }

  async login(user: User) {
    return {
      accessToken: await this.generateAccessToken(user),
      refreshToken: await this.generateRefreshToken(user),
    };
  }

  async refresh(user: User, refreshToken: string) {
    const payload = this.jwtService.verify(refreshToken, {
      secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
    });

    // TODO: refresh token db 검증
    if (user.id !== payload.id) {
      throw new UnauthorizedException('Invalid User');
    }

    return {
      accessToken: await this.generateAccessToken(user),
      refreshToken: await this.generateRefreshToken(user),
    };
  }

  async generateAccessToken(user: User): Promise<string> {
    return await this.jwtService.signAsync({
      email: user.email,
    });
  }

  async generateRefreshToken(user: User): Promise<string> {
    return await this.jwtService.signAsync(
      { id: user.id },
      {
        secret: this.configService.get<string>('JWT_REFRESH_SECRET'),
        expiresIn: this.configService.get<string>(
          'JWT_REFRESH_EXPIRATION_TIME',
        ),
      },
    );
  }

  async verifyToken(token: string) {
    try {
      const payload = this.jwtService.verify(token, {
        secret: this.configService.get<string>('JWT_SECRET_KEY'),
      });
      return {
        isVerified: !this.isExpired(payload.exp),
      };
    } catch (err) {
      return {
        isVerified: false,
      };
    }
  }

  async checkEmail(email: string) {
    const user = await this.userService.findUserByEmail(email);

    return {
      isAvailable: user ? false : true,
    };
  }

  async checkNickname(nickname: string) {
    const user = await this.userService.findUserByNickname(nickname);

    return {
      isAvailable: user ? false : true,
    };
  }

  isExpired(exp: number) {
    return exp < new Date().getTime() / 1000;
  }
}
