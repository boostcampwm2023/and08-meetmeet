import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Follow } from './entities/follow.entity';
import { Equal, Repository } from 'typeorm';
import { User } from '../user/entities/user.entity';
import { InviteService } from '../invite/invite.service';
import {
  AlreadyFollowException,
  FollowSelfException,
  NoSuchUserException,
  NotFollowingException,
} from './exception/follow.exception';

@Injectable()
export class FollowService {
  constructor(
    @InjectRepository(Follow) private followRepository: Repository<Follow>,
    @InjectRepository(User) private userRepository: Repository<User>,
    private readonly inviteService: InviteService,
  ) {}

  async getRawFollowers(user: User) {
    const followers = await this.followRepository.find({
      relations: {
        user: true,
        follower: true,
      },
      where: {
        follower: Equal(user.id),
      },
    });
    return followers;
  }

  async getRawFollowings(user: User) {
    const followings = await this.followRepository.find({
      relations: {
        user: true,
        follower: true,
      },
      where: {
        user: Equal(user.id),
      },
    });
    return followings;
  }

  async getFollowers(user: User) {
    const followers = await this.getRawFollowers(user);
    const followings = await this.getRawFollowings(user);

    const result: any[] = [];
    followers.map((follower) => {
      const user = {
        id: follower.user.id,
        nickname: follower.user.nickname,
        profile: follower.user.profile?.path ?? null,
        isFollowed: followings.some(
          (following) => following.follower.id === follower.user.id,
        )
          ? true
          : false,
      };
      result.push(user);
    });
    return { users: result };
  }

  async getFollowings(user: User) {
    const followings = await this.getRawFollowings(user);

    const result: any[] = [];
    followings.map((following) => {
      const user = {
        id: following.follower.id,
        nickname: following.follower.nickname,
        profile: following.follower.profile?.path ?? null,
        isFollowed: true,
      };
      result.push(user);
    });
    return { users: result };
  }

  async follow(user: User, userId: number) {
    if (user.id === userId) {
      throw new FollowSelfException();
    }

    const followingUser = await this.userRepository.findOne({
      where: {
        id: userId,
      },
    });

    if (!followingUser) {
      throw new NoSuchUserException();
    }

    const existingFollow = await this.followRepository.findOne({
      where: {
        user: Equal(user.id),
        follower: Equal(followingUser.id),
      },
    });

    if (existingFollow) {
      throw new AlreadyFollowException();
    }

    const follow = this.followRepository.create({
      user: user,
      follower: followingUser,
    });
    await this.followRepository.save(follow);

    if (followingUser.fcmToken) {
      await this.inviteService.sendFollowMessage(
        user,
        followingUser,
        followingUser.fcmToken,
      );
    }
    // todo : fcm 알람을 디비에 저장해야하나 고민.

    return { message: '팔로우 성공' };
  }

  async unfollow(user: User, userId: number) {
    const followingUser = await this.userRepository.findOne({
      where: {
        id: userId,
      },
    });

    if (!followingUser) {
      throw new NoSuchUserException();
    }

    const existingFollow = await this.followRepository.findOne({
      where: {
        user: Equal(user.id),
        follower: Equal(followingUser.id),
      },
    });

    if (!existingFollow) {
      throw new NotFollowingException();
    }

    await this.followRepository.softRemove(existingFollow);
    return { message: '언팔로우 성공' };
  }

  async isFollowed(user: User, userId: number) {
    const isFollowed = await this.followRepository.findOne({
      where: {
        user: Equal(user.id),
        follower: Equal(userId),
      },
    });
    return isFollowed ? true : false;
  }
}
