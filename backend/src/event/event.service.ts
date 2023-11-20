import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Between, LessThan, MoreThan, Repository } from 'typeorm';
import { Event } from './entities/event.entity';
import { User } from '../user/entities/user.entity';
import { CreateScheduleDto } from './dto/createSchedule.dto';
import { CalendarService } from '../calendar/calendar.service';

@Injectable()
export class EventService {
  constructor(
    @InjectRepository(Event)
    private eventRepository: Repository<Event>,
    private calendarService: CalendarService,
  ) {}

  async getEvents(startDate: string, endDate: string) {
    return await this.eventRepository.find({
      where: [
        {
          startDate: Between(new Date(startDate), new Date(endDate)),
        },
        { endDate: Between(new Date(startDate), new Date(endDate)) },
        {
          startDate: LessThan(new Date(startDate)),
          endDate: MoreThan(new Date(endDate)),
        },
      ],
    });
  }

  async createEvent(user: User, createScheduleDto: CreateScheduleDto) {
    // todo: calendar 어떻게 처리할지 의논
    const calendar = await this.calendarService.getCalendarByUserId(user.id);
    const event = this.eventRepository.create({
      ...createScheduleDto,
      calendar,
    });
    console.log(event);
    return await this.eventRepository.save(event);
  }
}
