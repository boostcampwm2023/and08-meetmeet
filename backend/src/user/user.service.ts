import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { hash } from 'bcrypt';
import { User } from './entities/user.entity';
import { OauthProvider } from './entities/oauthProvider.entity';
import { ContentService } from 'src/content/content.service';
import { FollowService } from '../follow/follow.service';
import { InviteService } from '../invite/invite.service';
import { SearchResponseDto } from '../event/dto/search-response.dto';
import {
  DuplicatedNicknameException,
  NoSuchOauthProviderException,
  QueryIntException,
  UserNotFoundException,
} from './exception/user.exception';

const SALTROUND = 10;

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    @InjectRepository(OauthProvider)
    private oauthProviderRepository: Repository<OauthProvider>,
    private readonly contentService: ContentService,
    private readonly followService: FollowService,
    private readonly inviteService: InviteService,
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
      throw new NoSuchOauthProviderException();
    }

    const user = this.userRepository.create({
      email: email,
      password: hashedPassword,
      nickname: nickname,
    });
    user.oauthProvider = oauthProvider;

    return await this.userRepository.save(user);
  }

  async getUserById(id: number) {
    return await this.userRepository
      .createQueryBuilder('user')
      .select([
        'user.email',
        'user.nickname',
        'profile.path',
        'oauth.displayName',
      ])
      .leftJoin('user.profile', 'profile')
      .leftJoin('user.oauthProvider', 'oauth')
      .where('user.id = :id', { id })
      .getOne();
  }

  async getUserInfo(user: User) {
    const result = await this.getUserById(user.id);

    if (!result) {
      throw new UserNotFoundException();
    }

    return {
      email: result.oauthProvider?.displayName ?? result.email,
      nickname: result.nickname,
      profile: result.profile?.path ?? null,
    };
  }

  async updateUserPassword(user: User, password: string) {
    const hashedPassword = await hash(password, SALTROUND);

    await this.userRepository.update(user.id, { password: hashedPassword });
    return await this.userRepository.findOne({ where: { id: user.id } });
  }

  async updateUserNickname(user: User, nickname: string) {
    if (await this.findUserByNickname(nickname)) {
      throw new DuplicatedNicknameException();
    }

    await this.userRepository.update(user.id, { nickname: nickname });
  }

  async updateUserProfile(user: User, profileImage: Express.Multer.File) {
    if (!profileImage) {
      await this.userRepository.update(user.id, { profileId: null });
      if (user.profile?.id) {
        await this.contentService.softDeleteContent([user.profile.id]);
      }
      return;
    }
    const userProfile = await this.updateProfileImage(
      user.profileId,
      profileImage,
    );

    await this.userRepository.update(user.id, { profile: userProfile });
  }

  async updateProfileImage(
    profileId: number | null,
    profileImage: Express.Multer.File,
  ) {
    if (!profileId) {
      return await this.contentService.createContent(profileImage, 'user');
    }

    const newProfile = await this.contentService.createContent(
      profileImage,
      'user',
    );
    await this.contentService.softDeleteContent([profileId]);

    return newProfile;
  }

  async deleteUser(user: User) {
    await this.userRepository.softDelete(user.id);
  }

  async findUserByOAuth(email: string, oauthProvider: string) {
    return await this.userRepository
      .createQueryBuilder('user')
      .leftJoinAndSelect('user.oauthProvider', 'oauth')
      .addSelect('user.deletedAt')
      .where('oauth.displayName = :oauth', { oauth: oauthProvider })
      .andWhere('user.email = :email', { email: email })
      .withDeleted()
      .getOne();
  }

  async searchUser(user: User, nickname: string) {
    const searchResult = await this.findUserByNickname(nickname);

    if (!searchResult) {
      return SearchResponseDto.of([], [], []);
    } else {
      return SearchResponseDto.of(
        [searchResult],
        [await this.followService.isFollowed(user, searchResult.id)],
        [],
      );
    }
  }

  async findUserWithPasswordByEmail(email: string) {
    return await this.userRepository.findOne({
      where: { email: email },
      select: ['id', 'nickname', 'email', 'password'],
    });
  }

  async findUserByEmail(email: string) {
    return await this.userRepository.findOne({
      where: { email: email },
      withDeleted: true,
    });
  }

  async findUserByNickname(nickname: string) {
    return await this.userRepository.findOne({
      where: { nickname: nickname },
      withDeleted: true,
    });
  }

  async findUserById(id: number) {
    return await this.userRepository.findOne({ where: { id: id } });
  }

  async registerFCMToken(user: User, token: string) {
    await this.userRepository.update(user.id, { fcmToken: token });
  }

  async logout(user: User) {
    await this.userRepository.update(user.id, { fcmToken: null });
  }

  async getUserNotification(user: User, page: string) {
    const notifications = await this.inviteService.getInvitesByUser(user);
    const result: any[] = [];
    let type: string;
    if (page === 'follow') {
      type = 'FOLLOW';
    } else if (page === 'invite') {
      type = 'EVENT_INVITATION';
    }
    notifications.forEach((notification) => {
      if (page === 'follow') {
        if (notification.event?.id === undefined) {
          const data = {
            type: type,
            body: {
              inviteId: notification.id,
              id: notification.sender.id,
              nickname: notification.sender.nickname,
              profile: notification.sender.profile?.path ?? null,
              status: notification.status.displayName,
            },
          };
          result.push(data);
        }
      } else if (page === 'invite') {
        if (notification.event?.id === undefined) {
          return;
        }

        if (notification.event.id) {
          const data = {
            type: type,
            body: {
              inviteId: notification.id,
              id: notification.sender.id,
              nickname: notification.sender.nickname,
              profile: notification.sender.profile?.path ?? null,
              eventId: notification.event?.id,
              title: notification.event?.title,
              startDate: notification.event?.startDate,
              endDate: notification.event?.endDate,
              status: notification.status.displayName,
            },
          };
          result.push(data);
        }
      }
    });
    return result;
  }

  async deleteUserNotification(user: User, ids: string) {
    const idList = this.parseNumberString(ids);
    await Promise.all(
      idList.map(async (id) => {
        await this.inviteService.deleteInvite(id, user);
      }),
    );
  }

  private parseNumberString(numberString: string) {
    return numberString.split(',').map((item) => {
      const parsedNumber = parseInt(item.trim(), 10);
      if (isNaN(parsedNumber)) {
        throw new QueryIntException();
      }
      return parsedNumber;
    });
  }
}
