import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Calendar } from './entities/calendar.entity';
import { User } from '../user/entities/user.entity';

@Injectable()
export class CalendarService {
  constructor(
    @InjectRepository(Calendar)
    private calendarRepository: Repository<Calendar>,
  ) {}

  findAll(): Promise<Calendar[]> {
    return this.calendarRepository.find();
  }

  createCalendar(user: User): Promise<Calendar> {
    const calendar = new Calendar();
    calendar.user = user;
    return this.calendarRepository.save(calendar);
  }

  async getCalendarByUserId(userId: number): Promise<Calendar> {
    const calendar = await this.calendarRepository.findOne({
      where: { user: { id: userId } },
    });

    if (!calendar) {
      return await this.createCalendar({ id: userId } as User);
    }

    return calendar;
  }
}
