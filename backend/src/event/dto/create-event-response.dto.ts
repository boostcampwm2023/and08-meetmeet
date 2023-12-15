import { ApiProperty } from '@nestjs/swagger';
import { EventMember } from 'src/event-member/entities/eventMember.entity';

export class CreateEventResponse {
  @ApiProperty()
  id: number;
  @ApiProperty()
  title: string;
  @ApiProperty()
  startDate: string;
  @ApiProperty()
  endDate: string;
  @ApiProperty()
  authority: string | null;
  @ApiProperty()
  isJoinable: boolean;
  @ApiProperty()
  repeatPolicyId: number | null;
  @ApiProperty()
  announcement: string | null;
  @ApiProperty()
  isVisible: boolean;
  @ApiProperty()
  memo: string | null;
  @ApiProperty()
  color: number;
  @ApiProperty()
  alarmMinutes: number;

  static of(eventMember: EventMember): CreateEventResponse {
    return {
      id: eventMember.event.id,
      startDate: eventMember.event.startDate.toISOString(),
      endDate: eventMember.event.endDate.toISOString(),
      title: eventMember.event.title,
      repeatPolicyId: eventMember.event.repeatPolicyId,
      isJoinable: eventMember.event.isJoinable ? true : false,
      announcement: eventMember.event.announcement,
      isVisible: eventMember.detail.isVisible ? true : false,
      memo: eventMember.detail.memo,
      color: eventMember.detail.color,
      alarmMinutes: eventMember.detail.alarmMinutes,
      authority: eventMember.authority.displayName,
    };
  }
}
