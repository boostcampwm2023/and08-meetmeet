import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Event } from './entities/event.entity';
import { RepeatPolicy } from './entities/repeatPolicy.entity';
import { EventController } from './event.controller';
import { EventService } from './event.service';
import { CalendarModule } from '../calendar/calendar.module';
import { CalendarService } from '../calendar/calendar.service';
import { Calendar } from '../calendar/entities/calendar.entity';
import { DetailService } from '../detail/detail.service';
import { Detail } from '../detail/entities/detail.entity';
import { Authority } from '../event-member/entities/authority.entity';
import { EventMemberService } from '../event-member/event-member.service';
import { EventMember } from '../event-member/entities/eventMember.entity';
import { FollowModule } from '../follow/follow.module';
import { UserModule } from '../user/user.module';
import { InviteModule } from '../invite/invite.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([
      Event,
      RepeatPolicy,
      Calendar,
      Detail,
      Authority,
      EventMember,
    ]),
    CalendarModule,
    FollowModule,
    UserModule,
    InviteModule,
  ],
  controllers: [EventController],
  providers: [EventService, CalendarService, DetailService, EventMemberService],
})
export class EventModule {}
