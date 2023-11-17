import { Controller, Get } from '@nestjs/common';
import { CalendarService } from './calendar.service';
import { Calendar } from './entities/calendar.entity';

@Controller('calendar')
export class CalendarController {
  constructor(private readonly calendarService: CalendarService) {}

  @Get()
  async getEvents(): Promise<Calendar[]> {
    return await this.calendarService.findAll();
  }
}
