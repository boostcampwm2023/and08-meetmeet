import { Controller, Get, Param } from '@nestjs/common';
import { CalendarService } from './calendar.service';
import { EventEntity } from './event.entity';

@Controller('calendar')
export class CalendarController {
  constructor(private readonly calendarService: CalendarService) {}

  @Get()
  async getEvents(
    @Param('startDate') startDate: string,
    @Param('endDate') endDate: string,
  ): Promise<EventEntity[]> {
    return await this.calendarService.findAll();
  }
}
