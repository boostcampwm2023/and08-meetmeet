import { Injectable } from '@nestjs/common';
import { EventMember } from './entities/eventMember.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Between, Repository } from 'typeorm';
import { User } from '../user/entities/user.entity';
import { Detail } from '../detail/entities/detail.entity';
import { Authority } from './entities/authority.entity';
import { Event } from '../event/entities/event.entity';

@Injectable()
export class EventMemberService {
  constructor(
    @InjectRepository(EventMember)
    private eventMemberRepository: Repository<EventMember>,
    @InjectRepository(Authority)
    private authorityRepository: Repository<Authority>,
  ) {}

  async getAuthorityId(displayName: string) {
    return await this.authorityRepository.findOneBy({
      displayName: displayName,
    });
  }

  async getEventByUser(user: User, startDate: Date, endDate: Date) {
    if (!user) return null;
    const eventMembers = await this.eventMemberRepository.find({
      relations: ['event'],
      where: {
        user: { id: user.id },
        event: { startDate: Between(startDate, endDate) },
      },
    });
    return eventMembers.map((eventMember) => eventMember.event);
  }

  async createEventMember(
    event: Event,
    user: User,
    detail: Detail,
    authority: Authority,
  ): Promise<void> {
    const eventMember = this.eventMemberRepository.create({
      user: user,
      detail: detail,
      authority: authority,
      event: event,
    });
    await this.eventMemberRepository.save(eventMember);
  }
}
