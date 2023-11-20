import { BadRequestException, Injectable } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { v4 as uuidv4 } from 'uuid';
import { compare } from 'bcrypt';
import { UserService } from 'src/user/user.service';
import { AuthUserDto } from './dto/auth-user.dto';

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
      // TODO: email 중복 확인
      return;
    }

    const nickname = uuidv4().split('-').at(0)!;
    // TODO: nickname 중복 확인

    this.userService.localCreateUser(email, password, nickname);
  }

  async validateUser(email: string, password: string) {
    const user = await this.userService.findUserByEmail(email);

    if (!user || !(await compare(password, user.password))) {
      throw new BadRequestException();
    }

    return user;
  }

  async login(authUserDto: AuthUserDto) {
    return {
      accessToken: await this.jwtService.signAsync({
        email: authUserDto.email,
      }),
    };
  }
}
