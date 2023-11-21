import {
  BadRequestException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { hash } from 'bcrypt';
import { User } from './entities/user.entity';
import { OauthProvider } from './entities/oauthProvider.entity';
import { UpdateUserDto } from './dto/update-user.dto';

const SALTROUND = 10;

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    @InjectRepository(OauthProvider)
    private oauthProviderRepository: Repository<OauthProvider>,
  ) {}

  async localCreateUser(email: string, password: string, nickname: string) {
    const hashedPassword = await hash(password, SALTROUND);

    const user = this.userRepository.create({
      email: email,
      password: hashedPassword,
      nickname: nickname,
    });

    return await this.userRepository.save(user);
  }

  async oauthCreateUser(email: string, nickname: string, oauth: string) {
    const hashedPassword = await hash(oauth + email, SALTROUND);
    const oauthProvider = await this.oauthProviderRepository.findOne({
      where: { displayName: oauth },
    });

    if (!oauthProvider) {
      throw new BadRequestException();
    }

    const user = this.userRepository.create({
      email: email,
      password: hashedPassword,
      nickname: nickname,
    });
    user.oauthProvider = oauthProvider;

    return await this.userRepository.save(user);
  }

  async updateUser(id: number, user: User, updateUserDto: UpdateUserDto) {
    if (id !== user.id) {
      throw new UnauthorizedException('잘못된 유저입니다.');
    }
    if (updateUserDto.password) {
      updateUserDto.password = await hash(updateUserDto.password, SALTROUND);
    }

    await this.userRepository.update(id, updateUserDto);
    return await this.userRepository.findOne({ where: { id: id } });
  }

  async deleteUser(id: number, user: User) {
    if (id !== user.id) {
      throw new UnauthorizedException('잘못된 유저입니다.');
    }

    await this.userRepository.softDelete(id);
  }

  async findUserByOAuth(email: string, oauthProvider: string) {
    const result = await this.userRepository
      .createQueryBuilder('user')
      .leftJoinAndSelect('user.oauthProvider', 'oauth')
      .where('oauth.displayName = :oauth', { oauth: oauthProvider })
      .andWhere('user.email = :email', { email: email })
      .getOne();

    return result;
  }

  async findUserByEmail(email: string) {
    return await this.userRepository.findOne({
      where: { email: email },
      select: ['id', 'nickname', 'email', 'password'],
    });
  }

  async findUserByNickname(nickname: string) {
    return await this.userRepository.findOne({ where: { nickname: nickname } });
  }
}
