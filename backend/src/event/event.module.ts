import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Event } from './entities/event.entity';
import { RepeatPolicy } from './entities/repeatPolicy.entity';
import { EventController } from './event.controller';
import { EventService } from './event.service';

@Module({
  imports: [TypeOrmModule.forFeature([Event, RepeatPolicy])],
  controllers: [EventController],
  providers: [EventService],
})
export class EventModule {}
