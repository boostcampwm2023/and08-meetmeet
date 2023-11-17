import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Event } from './entities/event.entity';
import { RepeatPolicy } from './entities/repeatPolicy.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Event, RepeatPolicy])],
})
export class EventModule {}
