import { Injectable } from '@nestjs/common';
import { CreateEventDto } from './dto/create-event.dto';
import { IEvent } from './event.entity';

@Injectable()
export class CalendarService {
  private events: IEvent[] = [];

  getEvents(startDate: string, endDate: string): IEvent[] {
    // TODO: 기간 확인
    return this.events;
  }

  createEvent(createEventDto: CreateEventDto) {
    const { title, startDate, endDate, isJoinable, isVisible, memo } =
      createEventDto;

    // TODO: repeat event일 경우
    const event: IEvent = {
      id: this.events.length + 1,
      title,
      startDate: new Date(startDate),
      endDate: new Date(endDate),
      isJoinable,
      isVisible,
      memo,
    };

    this.events.push(event);
  }
}
