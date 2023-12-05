import { ApiProperty } from '@nestjs/swagger';
import { User } from '../entities/user.entity';

export class UserResponse {
  @ApiProperty()
  id: number;
  @ApiProperty()
  nickname: string;
  @ApiProperty()
  profile: string | null;

  static from(user: User): UserResponse {
    return {
      id: user.id,
      nickname: user.nickname,
      profile: user.profile?.path ?? null,
    };
  }
}
