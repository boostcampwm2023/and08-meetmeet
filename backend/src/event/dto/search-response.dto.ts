import { ApiProperty } from '@nestjs/swagger';
import { User } from '../../user/entities/user.entity';
import { StatusEnum } from '../../invite/entities/status.enum';

export class SearchResponseDto {
  @ApiProperty()
  users: SingleSearchUser[];
  static of(users: User[], isFollowed: boolean[], isJoined: StatusEnum[]) {
    const res: SingleSearchUser[] = [];
    users.forEach((user, index) => {
      res.push({
        id: user.id,
        nickname: user.nickname,
        profile: user.profile?.path ?? null,
        isFollowed: isFollowed[index] ?? null,
        isJoined: isJoined[index] ?? null,
      });
    });
    return { users: res };
  }
}

class SingleSearchUser {
  @ApiProperty()
  id: number;

  @ApiProperty()
  nickname: string;

  @ApiProperty()
  profile: string | null;

  @ApiProperty()
  isFollowed: boolean;

  @ApiProperty()
  isJoined: StatusEnum;
}
