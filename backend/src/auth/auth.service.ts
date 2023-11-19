import { Injectable } from '@nestjs/common';
import { v4 as uuidv4 } from 'uuid';
import { UserService } from 'src/user/user.service';
import { RegisterDto } from './dto/register.dto';

@Injectable()
export class AuthService {
  constructor(private userService: UserService) {}

  async register(registerDto: RegisterDto) {
    const { email, password } = registerDto;

    const existedUser = await this.userService.findUserByEmail(email);
    if (existedUser) {
      // TODO: email 중복 확인
      return;
    }

    const nickname = uuidv4().split('-').at(0)!;
    // TODO: nickname 중복 확인

    this.userService.createUser(email, password, nickname);
  }
}
