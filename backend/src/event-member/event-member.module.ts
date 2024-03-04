import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { EventMember } from './entities/eventMember.entity';
import { Authority } from './entities/authority.entity';
import { EventMemberService } from './event-member.service';
import { DetailModule } from 'src/detail/detail.module';

@Module({
  imports: [TypeOrmModule.forFeature([EventMember, Authority]), DetailModule],
  providers: [EventMemberService],
  exports: [EventMemberService],
})
export class EventMemberModule {}
