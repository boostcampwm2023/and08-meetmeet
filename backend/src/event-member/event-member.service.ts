import { Injectable } from '@nestjs/common';
import { EventMember } from './entities/eventMember.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { In, MoreThanOrEqual, Repository } from 'typeorm';
import { User } from '../user/entities/user.entity';
import { Detail } from '../detail/entities/detail.entity';
import { Authority } from './entities/authority.entity';
import { Event } from '../event/entities/event.entity';
import { DetailService } from 'src/detail/detail.service';
import { EventDetailNotFoundException } from 'src/event/exception/event.exception';

@Injectable()
export class EventMemberService {
  constructor(
    @InjectRepository(EventMember)
    private eventMemberRepository: Repository<EventMember>,
    @InjectRepository(Authority)
    private authorityRepository: Repository<Authority>,
    private readonly detailService: DetailService,
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

  async getEventMemberByUserIdAndEventId(userId: number, eventId: number) {
    return await this.eventMemberRepository.findOne({
      where: { user: { id: userId }, event: { id: eventId } },
    });
  }

  async deleteEventMembers(eventMembers: EventMember[]) {
    if (!eventMembers.length) {
      return;
    }
    await this.detailService.deleteDetailsById(
      eventMembers.map((eventMember) => {
        if (!eventMember.detail) {
          throw new EventDetailNotFoundException();
        }
        return eventMember.detail.id;
      }),
    );

    await this.eventMemberRepository.softDelete(
      eventMembers.map((eventMember) => eventMember.id),
    );
  }

  async deleteEventMembersByEventIds(eventIds: number[]) {
    const eventMembers = await this.eventMemberRepository
      .createQueryBuilder('em')
      .leftJoinAndSelect('em.detail', 'detail')
      .leftJoinAndSelect('em.event', 'event')
      .where({ event: { id: In(eventIds) } })
      .getMany();

    await this.deleteEventMembers(eventMembers);
  }

  async getEventMembersByStartDateAndRepeatPolicy(
    userId: number,
    startDate: Date,
    repeatPolicyId: number,
  ) {
    return await this.eventMemberRepository.find({
      relations: ['event', 'user', 'detail'],
      where: {
        user: { id: userId },
        event: {
          repeatPolicyId: repeatPolicyId,
          startDate: MoreThanOrEqual(startDate),
        },
      },
    });
  }
}
