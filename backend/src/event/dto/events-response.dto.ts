import { ApiProperty } from '@nestjs/swagger';
import { EventMember } from 'src/event-member/entities/eventMember.entity';
import { Event } from '../entities/event.entity';
import { EventResponse, Member } from './event-response';

export class EventsResponseDto extends EventResponse {
  @ApiProperty()
  repeatPolicyId: number | null;
  @ApiProperty()
  color: number;
  @ApiProperty()
  alarmMinutes: number;

  static of(event: Event, memberDetail: EventMember): EventsResponseDto {
    return {
      id: event.id,
      title: event.title,
      startDate: event.startDate.toISOString(),
      endDate: event.endDate.toISOString(),
      eventMembers: event.eventMembers.map((eventMember) =>
        Member.of(eventMember),
      ),
      isJoinable: event.isJoinable ? true : false,
      repeatPolicyId: event.repeatPolicyId ?? null,
      authority: memberDetail.authority.displayName,
      color: memberDetail.detail.color,
      alarmMinutes: memberDetail.detail.alarmMinutes,
    };
  }
}
