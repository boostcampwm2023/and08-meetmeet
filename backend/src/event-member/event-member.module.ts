import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { EventMember } from './entities/eventMember.entity';

@Module({ imports: [TypeOrmModule.forFeature([EventMember])] })
export class EventMemberModule {}
