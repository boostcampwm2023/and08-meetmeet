import { ApiProperty } from '@nestjs/swagger';
import { EventMember } from 'src/event-member/entities/eventMember.entity';
import { Event } from '../entities/event.entity';
import { RepeatPolicy } from '../entities/repeatPolicy.entity';
import { EventResponse, Member } from './event-response';

const getRepeatPolicy = (repeatPolicy: RepeatPolicy) => {
  if (!repeatPolicy) {
    return {
      repeatTerm: null,
      repeatFrequency: null,
      repeatEndDate: null,
    };
  }

  const repeat = {
    repeatDay: 'DAY',
    repeatWeek: 'WEEK',
    repeatMonth: 'MONTH',
    repeatYear: 'YEAR',
  };

  const key = Object.keys(repeat).find(
    (key: keyof typeof repeat) => repeatPolicy[key],
  ) as keyof typeof repeat;

  return {
    repeatTerm: repeat[key],
    repeatFrequency: repeatPolicy[key],
    repeatEndDate: repeatPolicy.endDate.toISOString(),
  };
};

export class EventResponseDto extends EventResponse {
  @ApiProperty()
  color: number;
  @ApiProperty()
  alarmMinutes: number;
  @ApiProperty()
  memo: string;
  @ApiProperty()
  isVisible: boolean;
  @ApiProperty()
  repeatTerm: string | null = null;
  @ApiProperty()
  repeatFrequency: number | null = null;
  @ApiProperty()
  repeatEndDate: string | null = null;

  static of(event: Event, memberDetail: EventMember): EventResponseDto {
    return {
      id: event.id,
      title: event.title,
      startDate: event.startDate.toISOString(),
      endDate: event.endDate.toISOString(),
      eventMembers: event.eventMembers.map((eventMember) =>
        Member.of(eventMember),
      ),
      isJoinable: event.isJoinable ? true : false,
      authority: memberDetail.authority.displayName,
      color: memberDetail.detail.color,
      alarmMinutes: memberDetail.detail.alarmMinutes,
      memo: memberDetail.detail.memo,
      isVisible: memberDetail.detail.isVisible ? true : false,
      ...getRepeatPolicy(event.repeatPolicy),
    };
  }
}
