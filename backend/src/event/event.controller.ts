import { Controller, Get, Query } from '@nestjs/common';
import { Event } from './entities/event.entity';
import { EventService } from './event.service';

@Controller('events')
export class EventController {
  constructor(private readonly eventService: EventService) {}

  @Get()
  async getEvents(
    @Query('startDate') startDate: string,
    @Query('endDate') endDate: string,
  ): Promise<Event[]> {
    return await this.eventService.getEvents(startDate, endDate);
  }
}
