import { BadRequestException, Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { v4 as uuidv4 } from 'uuid';
import { compare } from 'bcrypt';
import { UserService } from 'src/user/user.service';
import { AuthUserDto } from './dto/auth-user.dto';
import { User } from 'src/user/entities/user.entity';

@Injectable()
export class AuthService {
  constructor(
    private userService: UserService,
    private jwtService: JwtService,
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
    const user = await this.userService.findUserByEmail(email);

    if (!user || !(await compare(password, user.password))) {
      throw new BadRequestException();
    }

    return user;
  }

  async login(user: User) {
    return {
      accessToken: await this.jwtService.signAsync(
        {
          nickname: user.nickname,
        },
        { expiresIn: '5m' },
      ),
      refreshToken: await this.jwtService.signAsync(
        {
          nickname: user.nickname,
        },
        { expiresIn: '7d' },
      ),
    };
  }

  async refresh(user: User) {
    return {
      accessToken: await this.jwtService.signAsync(
        {
          nickname: user.nickname,
        },
        { expiresIn: '5m' },
      ),
      refreshToken: await this.jwtService.signAsync(
        {
          nickname: user.nickname,
        },
        { expiresIn: '7d' },
      ),
    };
  }
}
