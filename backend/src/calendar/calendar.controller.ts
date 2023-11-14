import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { CalendarService } from './calendar.service';
import { CreateEventDto } from './dto/create-event.dto';
import { IEvent } from './event.entity';

@Controller('calendar')
export class CalendarController {
  constructor(private readonly calendarService: CalendarService) {}

  @Get()
  getEvents(
    @Param('startDate') startDate: string,
    @Param('endDate') endDate: string,
  ): IEvent[] {
    return this.calendarService.getEvents(startDate, endDate);
  }

  @Post('events')
  @UsePipes(ValidationPipe)
  createEvent(@Body() createEventDto: CreateEventDto) {
    return this.calendarService.createEvent(createEventDto);
  }
}
