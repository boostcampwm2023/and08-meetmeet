import {Controller} from '@nestjs/common';
import {CalendarService} from './calendar.service';

@Controller('calendar')
export class CalendarController {
  constructor(private readonly calendarService: CalendarService) {}
}
