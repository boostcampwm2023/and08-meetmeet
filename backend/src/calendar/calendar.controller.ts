import { Controller, Get, Param } from '@nestjs/common';
import { CalendarService } from './calendar.service';
import { Event } from '../event/entities/event.entity';

@Controller('calendar')
export class CalendarController {
  constructor(private readonly calendarService: CalendarService) {}

  @Get()
  async getEvents(
    @Param('startDate') startDate: string,
    @Param('endDate') endDate: string,
  ): Promise<Event[]> {
    return await this.calendarService.findAll();
  }
}
