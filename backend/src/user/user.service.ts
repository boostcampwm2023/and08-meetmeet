import {
  BadRequestException,
  Injectable,
  InternalServerErrorException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { hash } from 'bcrypt';
import { User } from './entities/user.entity';
import { OauthProvider } from './entities/oauthProvider.entity';
import { UpdateUserInfoDto } from './dto/update-user-info.dto';
import { ContentService } from 'src/content/content.service';

const SALTROUND = 10;

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    @InjectRepository(OauthProvider)
    private oauthProviderRepository: Repository<OauthProvider>,
    private readonly contentService: ContentService,
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

  async getUserInfo(user: User) {
    const result = await this.userRepository
      .createQueryBuilder('user')
      .select([
        'user.email',
        'user.nickname',
        'profile.path',
        'oauth.displayName',
      ])
      .leftJoin('user.profile', 'profile')
      .leftJoin('user.oauthProvider', 'oauth')
      .where('user.id = :id', { id: user.id })
      .getOne();

    const userInfo = {
      email: result?.oauthProvider?.displayName ?? result?.email,
      nickname: result?.nickname,
      profileUrl: result?.profile?.path ?? null,
    };

    return userInfo;
  }

  async updateUserPassword(user: User, password: string) {
    const hashedPassword = await hash(password, SALTROUND);

    await this.userRepository.update(user.id, { password: hashedPassword });
    return await this.userRepository.findOne({ where: { id: user.id } });
  }

  async updateUserInfo(
    user: User,
    updateUserDto: UpdateUserInfoDto,
    profileImage: Express.Multer.File,
  ) {
    if (
      updateUserDto.nickname &&
      (await this.findUserByNickname(updateUserDto.nickname))
    ) {
      throw new BadRequestException('중복된 닉네임입니다.');
    }
    if (profileImage) {
      const userProfile = await this.updateProfileImage(
        user.profileId,
        profileImage,
      );

      user.profile = userProfile;
      user.profileId = userProfile.id;
    }

    await this.userRepository.update(user.id, updateUserDto);
  }

  async updateProfileImage(
    profileId: number | null,
    profileImage: Express.Multer.File,
  ) {
    if (!profileId) {
      return await this.contentService.createContent(profileImage, 'user');
    }

    const result = await this.contentService.updateContent(
      profileId,
      profileImage,
    );
    if (!result) {
      throw new InternalServerErrorException('프로필 변경에 실패했습니다.');
    }

    return result;
  }

  async deleteUser(user: User) {
    await this.userRepository.softDelete(user.id);
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

  async findUserWithPasswordByEmail(email: string) {
    return await this.userRepository.findOne({
      where: { email: email },
      select: ['id', 'nickname', 'email', 'password'],
    });
  }

  async findUserByEmail(email: string) {
    return await this.userRepository.findOne({ where: { email: email } });
  }

  async findUserByNickname(nickname: string) {
    return await this.userRepository.findOne({ where: { nickname: nickname } });
  }
}
