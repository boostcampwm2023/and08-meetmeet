import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { CalendarController } from './calendar/calendar.controller';
import { CalendarService } from './calendar/calendar.service';

@Module({
  imports: [],
  controllers: [AppController, CalendarController],
  providers: [AppService, CalendarService],
})
export class AppModule {}
