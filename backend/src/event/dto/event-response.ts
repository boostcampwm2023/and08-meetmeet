import { ApiProperty } from '@nestjs/swagger';
import { EventMember } from 'src/event-member/entities/eventMember.entity';
import { UserResponse } from 'src/user/dto/user-response';

export class Member extends UserResponse {
  @ApiProperty()
  authority: string;

  static of(eventMember: EventMember): Member {
    return {
      id: eventMember.user.id,
      nickname: eventMember.user.nickname,
      profile: eventMember.user.profile?.path ?? null,
      authority: eventMember.authority.displayName,
    };
  }
}

export class EventResponse {
  @ApiProperty()
  id: number;
  @ApiProperty()
  title: string;
  @ApiProperty()
  startDate: string;
  @ApiProperty()
  endDate: string;
  @ApiProperty({ type: Member })
  eventMembers: Member[];
  @ApiProperty()
  authority: string | null;
  @ApiProperty()
  isJoinable: boolean;
}
