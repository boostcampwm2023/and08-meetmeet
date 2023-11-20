import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Event } from './entities/event.entity';
import { RepeatPolicy } from './entities/repeatPolicy.entity';
import { EventController } from './event.controller';
import { EventService } from './event.service';
import { CalendarModule } from '../calendar/calendar.module';
import { CalendarService } from '../calendar/calendar.service';
import { Calendar } from '../calendar/entities/calendar.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([Event, RepeatPolicy, Calendar]),
    CalendarModule,
  ],
  controllers: [EventController],
  providers: [EventService, CalendarService],
})
export class EventModule {}
