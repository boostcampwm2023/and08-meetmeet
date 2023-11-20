import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { hash } from 'bcrypt';
import { User } from './entities/user.entity';

const SALTROUND = 10;

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
  ) {}

  async createUser(email: string, password: string, nickname: string) {
    const hashedPassword = await hash(password, SALTROUND);

    return await this.userRepository.save({
      email: email,
      password: hashedPassword,
      nickname: nickname,
    });
  }

  async findUserByEmail(email: string) {
    return await this.userRepository.findOne({ where: { email: email } });
  }
}
