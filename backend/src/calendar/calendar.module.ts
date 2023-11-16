import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CalendarController } from './calendar.controller';
import { Calendar } from './entities/calendar.entity';
import { CalendarService } from './calendar.service';
import { Event } from '../event/entities/event.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Event, Calendar])],
  controllers: [CalendarController],
  providers: [CalendarService],
})
export class CalendarModule {}
