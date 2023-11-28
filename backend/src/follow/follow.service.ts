import { HttpException, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Follow } from './entities/follow.entity';
import { Equal, Repository } from 'typeorm';
import { User } from '../user/entities/user.entity';

@Injectable()
export class FollowService {
  constructor(
    @InjectRepository(Follow) private followRepository: Repository<Follow>,
    @InjectRepository(User) private userRepository: Repository<User>,
  ) {}
  async getFollowers(user: User) {
    const followers = await this.followRepository.find({
      relations: {
        user: true,
        follower: true,
      },
      where: {
        follower: Equal(user.id),
      },
    });

    const followings = await this.followRepository.find({
      relations: {
        user: true,
        follower: true,
      },
      where: {
        user: Equal(user.id),
      },
    });

    const result: any[] = [];
    followers.map((follower) => {
      const user = {
        id: follower.user.id,
        nickname: follower.user.nickname,
        profile: follower.user.profileId,
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
    const followings = await this.followRepository.find({
      relations: {
        user: true,
        follower: true,
      },
      where: {
        user: Equal(user.id),
      },
    });
    const result: any[] = [];
    followings.map((following) => {
      const user = {
        id: following.follower.id,
        nickname: following.follower.nickname,
        profile: following.follower.profileId,
      };
      result.push(user);
    });
    return { users: result };
  }

  async follow(user: User, userId: number) {
    const followingUser = await this.userRepository.findOne({
      where: {
        id: userId,
      },
    });

    if (!followingUser) {
      throw new HttpException('존재하지 않는 유저입니다.', 400);
    }

    if (user.id === followingUser.id) {
      throw new HttpException('자기 자신을 팔로우할 수 없습니다.', 400);
    }

    const existingFollow = await this.followRepository.findOne({
      where: {
        user: Equal(user.id),
        follower: Equal(followingUser.id),
      },
    });

    if (existingFollow) {
      throw new HttpException('이미 팔로우한 유저입니다.', 400);
    }

    const follow = this.followRepository.create({
      user: user,
      follower: followingUser,
    });
    await this.followRepository.save(follow);
    return { message: '팔로우 성공' };
  }

  async unfollow(user: User, userId: number) {
    const followingUser = await this.userRepository.findOne({
      where: {
        id: userId,
      },
    });

    if (!followingUser) {
      throw new Error('존재하지 않는 유저입니다.');
    }

    const existingFollow = await this.followRepository.findOne({
      where: {
        user: Equal(user.id),
        follower: Equal(followingUser.id),
      },
    });

    if (!existingFollow) {
      throw new Error('팔로우되어 있지 않습니다.');
    }

    await this.followRepository.softRemove(existingFollow);
    return { message: '언팔로우 성공' };
  }
}
