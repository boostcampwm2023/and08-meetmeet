import { Injectable } from '@nestjs/common';
import { EventMember } from './entities/eventMember.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { In, Repository } from 'typeorm';
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

  async createEventMember(
    event: Event,
    user: User,
    detail: Detail,
    authority: Authority,
  ) {
    const eventMember = this.eventMemberRepository.create({
      user: user,
      detail: detail,
      authority: authority,
      event: event,
    });
    return await this.eventMemberRepository.save(eventMember);
  }

  async createEventMemberBulk(
    events: Event[],
    user: User,
    details: Detail[],
    authority: Authority,
  ) {
    const eventMembers: EventMember[] = [];
    for (let i = 0; i < events.length; i++) {
      eventMembers.push(
        this.eventMemberRepository.create({
          user: user,
          detail: details[i],
          authority: authority,
          event: events[i],
        }),
      );
    }

    const result = await this.eventMemberRepository.insert(eventMembers);
    return await this.eventMemberRepository.find({
      relations: ['event'],
      where: { id: In(result.identifiers.map((identifier) => identifier.id)) },
    });
  }

  async getAuthorityOfUserByEventId(eventId: number, userId: number) {
    const result = await this.eventMemberRepository.findOne({
      select: { authority: { displayName: true } },
      relations: ['authority'],
      where: { event: { id: eventId }, user: { id: userId } },
    });

    return result?.authority?.displayName;
  }

  async deleteEventMemberByEventId(event: Event) {
    const EventMembers = await this.eventMemberRepository.find({
      where: { event: { id: event.id } },
    });

    for (const eventMember of EventMembers) {
      await this.eventMemberRepository.softRemove(eventMember);
    }
  }
}
