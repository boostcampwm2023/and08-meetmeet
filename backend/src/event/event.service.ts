import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Event } from './entities/event.entity';
import { User } from '../user/entities/user.entity';
import { CreateScheduleDto, RepeatTerm } from './dto/createSchedule.dto';
import { CalendarService } from '../calendar/calendar.service';
import { RepeatPolicy } from './entities/repeatPolicy.entity';
import { DetailService } from '../detail/detail.service';
import { EventMemberService } from '../event-member/event-memer.service';
import { Calendar } from '../calendar/entities/calendar.entity';

@Injectable()
export class EventService {
  constructor(
    @InjectRepository(Event)
    private eventRepository: Repository<Event>,
    @InjectRepository(RepeatPolicy)
    private repeatPolicyRepository: Repository<RepeatPolicy>,
    private calendarService: CalendarService,
    private detailService: DetailService,
    private eventMemberService: EventMemberService,
  ) {}

  async getEvents(user: User, startDate: string, endDate: string) {
    const eventsWithMembersAndAuthority = await this.eventRepository
      .createQueryBuilder('event')
      .leftJoinAndSelect('event.eventMembers', 'eventMember')
      .leftJoinAndSelect('eventMember.user', 'user')
      .leftJoinAndSelect('eventMember.authority', 'authority')
      .where('user.id = :userId', { userId: user.id })
      .andWhere('event.startDate BETWEEN :startDate AND :endDate', {
        startDate,
        endDate,
      })
      .getMany();

    const result = eventsWithMembersAndAuthority.map((event) => {
      return {
        id: event.id,
        title: event.title,
        startDate: event.startDate,
        endDate: event.endDate,
        eventMembers: event.eventMembers.map((eventMember) => ({
          id: eventMember.id,
          nickname: eventMember.user.nickname,
          profile: `/user/profile/${eventMember.user.id}`,
          authority: eventMember.authority.displayName,
        })),
        authority:
          event.eventMembers.find(
            (eventMember) => eventMember.user.id === user.id,
          )?.authority?.displayName || null,
        repeatPolicyId: event.repeatPolicyId,
        isJoinable: event.isJoinable,
      };
    });

    if (!result) {
      return { events: [] };
    }
    return { events: result };
  }

  async createEvent(user: User, createScheduleDto: CreateScheduleDto) {
    // todo: calendar 어떻게 처리할지 의논
    const calendar = await this.calendarService.getCalendarByUserId(user.id);

    if (this.isReapeatPolicy(createScheduleDto)) {
      if (!this.isReapeatPolicyValid(createScheduleDto)) {
        throw new Error('repeat policy is not valid');
      }

      const repeatPolicy = await this.createRepeatPolicy(createScheduleDto);
      if (!repeatPolicy) {
        throw new Error('repeat policy is not valid');
      }

      const events = await this.createRepeatEvent(
        user,
        createScheduleDto,
        calendar,
        repeatPolicy,
      );
      const details = await this.detailService.createDetailBulk(
        createScheduleDto,
        events.length,
      );
      const authority = await this.eventMemberService.getAuthorityId('OWNER');
      if (!authority) {
        throw new Error('authority is not valid');
      }
      await this.eventMemberService.createEventMemberBulk(
        events,
        user,
        details,
        authority,
      );
    } else {
      const event = this.eventRepository.create({
        ...createScheduleDto,
        calendar,
      });
      const savedEvent = await this.eventRepository.save(event);
      const detail = await this.detailService.createDetail(createScheduleDto);
      const authority = await this.eventMemberService.getAuthorityId('OWNER');
      if (!authority) {
        throw new Error('authority is not valid');
      }
      await this.eventMemberService.createEventMember(
        savedEvent,
        user,
        detail,
        authority,
      );
    }
  }

  async deleteEvent(user: User, eventId: number) {
    const event = await this.eventRepository
      .createQueryBuilder('event')
      .leftJoinAndSelect('event.eventMembers', 'eventMember')
      .leftJoinAndSelect('eventMember.user', 'user')
      .leftJoinAndSelect('eventMember.authority', 'authority')
      .where('user.id = :userId', { userId: user.id })
      .andWhere('event.id = :eventId', { eventId })
      .getOne();

    const updatedEvent: any = { ...event, authority: undefined };
    if (!event) {
      throw new Error('event is not valid');
    }
    updatedEvent.deletedAt = new Date();
    event.eventMembers.forEach((eventMember) => {
      if (eventMember.user.id === user.id) {
        updatedEvent.authority = eventMember.authority.displayName;
      }
    });
    console.log(updatedEvent);
    if (updatedEvent.authority === 'OWNER') {
      await this.eventRepository.softRemove(event);
      await this.eventMemberService.deleteEventMemberByEventId(event);
    } else if (updatedEvent.authority === 'ADMIN') {
      // todo 운영자는 어떻게 할지 고민
    } else if (updatedEvent.authority === 'MEMBER') {
      await this.eventMemberService.deleteEventMemberByEventId(event);
    }
  }

  isReapeatPolicyValid(createScheduleDto: CreateScheduleDto) {
    if (!createScheduleDto.repeatTerm || !createScheduleDto.repeatFrequency) {
      return false;
    }

    if (
      createScheduleDto.repeatFrequency <= 0 ||
      createScheduleDto.repeatFrequency > 7
    ) {
      return false;
    }
    return true;
  }
  isReapeatPolicy(createScheduleDto: CreateScheduleDto) {
    if (createScheduleDto.repeatTerm) {
      return true;
    }
    return false;
  }

  async createRepeatPolicy(createScheduleDto: CreateScheduleDto) {
    const repeatPolicy = this.repeatPolicyRepository.create({
      startDate: createScheduleDto.startDate,
      endDate: createScheduleDto.endDate,
    });
    if (!createScheduleDto.repeatFrequency) {
      throw new Error('repeat frequency is not valid');
    }
    switch (createScheduleDto.repeatTerm) {
      case 'DAY':
        repeatPolicy.repeatDay = createScheduleDto.repeatFrequency;
        break;
      case 'WEEK':
        repeatPolicy.repeatWeek = createScheduleDto.repeatFrequency;
        break;
      case 'MONTH':
        repeatPolicy.repeatMonth = createScheduleDto.repeatFrequency;
        break;
      case 'YEAR':
        repeatPolicy.repeatYear = createScheduleDto.repeatFrequency;
        break;
    }
    return await this.repeatPolicyRepository.save(repeatPolicy);
  }

  formatDateString(date: string) {
    const year = date.substring(0, 4);
    const month = date.substring(4, 6);
    const day = date.substring(6, 8);

    const formattedDate = `${year}-${month}-${day}`;

    return formattedDate;
  }

  incrementDate(date: Date, repeatTerm: RepeatTerm, repeatFrequency: number) {
    switch (repeatTerm) {
      case RepeatTerm.DAY:
        date.setDate(date.getDate() + repeatFrequency);
        break;
      case RepeatTerm.WEEK:
        date.setDate(date.getDate() + 7 * repeatFrequency);
        break;
      case RepeatTerm.MONTH:
        date.setMonth(date.getMonth() + repeatFrequency);
        break;
      case RepeatTerm.YEAR:
        date.setFullYear(date.getFullYear() + repeatFrequency);
        break;
    }
  }

  async createRepeatEvent(
    user: User,
    createScheduleDto: CreateScheduleDto,
    calendar: Calendar,
    repeatPolicy: RepeatPolicy,
  ) {
    if (!createScheduleDto.repeatEndDate) {
      createScheduleDto.repeatEndDate = new Date('2038-01-18');
    }
    const endDate = new Date(createScheduleDto.repeatEndDate);

    const startDateCursor = new Date(createScheduleDto.startDate);
    const endDateCursor = new Date(createScheduleDto.endDate);
    const events = [];

    while (endDateCursor < endDate) {
      const event = this.eventRepository.create({
        ...createScheduleDto,
        calendar,
        repeatPolicy,
        startDate: startDateCursor,
        endDate: endDateCursor,
      });
      events.push(event);
      if (
        createScheduleDto.repeatTerm &&
        createScheduleDto.repeatFrequency != null
      ) {
        this.incrementDate(
          startDateCursor,
          createScheduleDto.repeatTerm,
          createScheduleDto.repeatFrequency,
        );
        this.incrementDate(
          endDateCursor,
          createScheduleDto.repeatTerm,
          createScheduleDto.repeatFrequency,
        );
      }
    }
    return await this.eventRepository.save(events);
  }
}
