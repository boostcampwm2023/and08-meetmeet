import {Injectable, UnauthorizedException} from '@nestjs/common';
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

  async deleteEvent(user: User, eventId: number, isAll: boolean) {
    // 단건 삭제
    const event = await this.eventRepository
      .createQueryBuilder('event')
      .leftJoinAndSelect('event.eventMembers', 'eventMember')
      .leftJoinAndSelect('eventMember.user', 'user')
      .leftJoinAndSelect('eventMember.authority', 'authority')
      .where('user.id = :userId', { userId: user.id })
      .andWhere('event.id = :eventId', { eventId })
      .getOne();

    if (
      event?.eventMembers.some(
        (eventMember) =>
          eventMember.user.id === user.id &&
          eventMember.authority.displayName !== 'OWNER',
      )
    ) {
      throw UnauthorizedException;
    }

    if (!isAll) {
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

      // todo feed 삭제는 건들지도 않았음...
      if (updatedEvent.authority === 'OWNER') {
        await this.eventRepository.softRemove(event);
        await this.eventMemberService.deleteEventMemberByEventId(event);
        await this.detailService.deleteDetail(event.eventMembers[0].detail);
      } else if (updatedEvent.authority === 'ADMIN') {
        // todo 운영자는 어떻게 할지 고민
      } else if (updatedEvent.authority === 'MEMBER') {
        await this.eventMemberService.deleteEventMemberByEventId(event);
        await this.detailService.deleteDetail(event.eventMembers[0].detail);
      }
    } else {
      const event = await this.eventRepository
          .createQueryBuilder('event')
          .leftJoinAndSelect('event.eventMembers', 'eventMember')
          .leftJoinAndSelect('eventMember.user', 'user')
          .leftJoinAndSelect('eventMember.authority', 'authority')
          .leftJoinAndSelect('eventMember.detail', 'detail')
          .leftJoinAndSelect('event.repeatPolicy', 'repeatPolicy')
          .where('user.id = :userId', { userId: user.id })
          .andWhere('event.id = :eventId', { eventId })
          .getOne();

      if (!event) {
        throw new Error('event is not valid');
      }

      const resultObject = event
          ? {
            id: event.id,
            title: event.title,
            startDate: event.startDate,
            endDate: event.endDate,
            eventMember: event.eventMembers,
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
            repeatPolicy: {
              repeatPolicyId: event.repeatPolicy?.id || null,
              repeatPolicyName: event.repeatPolicy,
              // RepeatPolicy의 다른 필드들을 여기에 추가할 수 있습니다.
            },
            isJoinable: event.isJoinable,
            detail: event.eventMembers[0].detail,
          }
          : null;


      console.log(event);
    }
  }

  async updateEvent(
    user: User,
    eventId: number,
    createScheduleDto: CreateScheduleDto,
    isAll: boolean,
  ) {
    // reapeatPolicy가 변경되면 -> 찾아서 삭제하고 새로 생성 -> 삭제할때 피드가 있으면 삭제하면 안된다.
    // RepeatPolicy가 변경되는지 부터 확인하자.
    const event = await this.eventRepository
      .createQueryBuilder('event')
      .leftJoinAndSelect('event.eventMembers', 'eventMember')
      .leftJoinAndSelect('eventMember.user', 'user')
      .leftJoinAndSelect('eventMember.authority', 'authority')
      .leftJoinAndSelect('eventMember.detail', 'detail')
      .leftJoinAndSelect('event.repeatPolicy', 'repeatPolicy')
      .where('user.id = :userId', { userId: user.id })
      .andWhere('event.id = :eventId', { eventId })
      .getOne();

    if (!event) {
      throw new Error('event is not valid');
    }
    const resultObject = event
      ? {
          id: event.id,
          title: event.title,
          startDate: event.startDate,
          endDate: event.endDate,
          eventMember: event.eventMembers,
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
          repeatPolicy: {
            repeatPolicyId: event.repeatPolicy?.id || null,
            repeatPolicyName: event.repeatPolicy,
            // RepeatPolicy의 다른 필드들을 여기에 추가할 수 있습니다.
          },
          isJoinable: event.isJoinable,
          detail: event.eventMembers[0].detail,
        }
      : null;

    if (!resultObject) {
      throw new Error('event is not valid');
    }
    const detail = resultObject.eventMember[0].detail;
    if (resultObject.authority === 'MEMBER') {
      await this.detailService.updateDetail(detail, createScheduleDto);
    } else if (resultObject.authority === 'OWNER') {
      if (resultObject.repeatPolicy.repeatPolicyId === null) {
        // 주인이고 반복일정이 아닌경우
        await this.detailService.updateDetail(detail, createScheduleDto);
        await this.eventRepository.save({
          ...event,
          ...createScheduleDto,
        });
      } else {
        // 주인이고 반복일정인 경우
        if (
          this.isEqualRepeatPolicy(
            resultObject.repeatPolicy.repeatPolicyName,
            createScheduleDto,
          )
        ) {
          // 반복일정이 이전과 같은경우
          await this.detailService.updateDetail(detail, createScheduleDto);
          await this.eventRepository.save({
            ...event,
            ...createScheduleDto,
          });
        } else {
          // 반복일정이 같지 않은 경우 -> 반복일정을 삭제하고 새로 생성(피드가 없는경우)
          const eventsWithRepeatPolicyAndNoFeed = await this.eventRepository
            .createQueryBuilder('event')
            .leftJoinAndSelect('event.eventMembers', 'eventMember')
            .leftJoinAndSelect('event.repeatPolicy', 'repeatPolicy')
            .leftJoinAndSelect('eventMember.detail', 'detail')
            .leftJoin('Feed', 'feed', 'feed.event = event.id')
            .where('event.repeatPolicy IS NOT NULL')
            .andWhere('feed.event IS NULL')
            .andWhere('repeatPolicy.id = :repeatPolicyId', {
              repeatPolicyId: resultObject.repeatPolicy.repeatPolicyId,
            })
            .getMany();

          // const deleteEvents = [];
          // const deleteDetails = [];
          // const deleteEventMembers = [];
          for (const event1 of eventsWithRepeatPolicyAndNoFeed) {
            //     // todo 일괄 처리되도록 수정해야한다.
            //     // deleteEvents.push(event1);
            //     // deleteDetails.push(event1.eventMembers[0].detail);
            //     // deleteEventMembers.push(event1.eventMembers[0]);
            await this.eventRepository.softRemove(event1);
            await this.eventMemberService.deleteEventMemberByEventId(event1);
            await this.detailService.deleteDetail(
              event1.eventMembers[0].detail,
            );
          }

          await this.repeatPolicyRepository.softRemove(
            resultObject.repeatPolicy.repeatPolicyName,
          );
          const repeatPolicy = await this.createRepeatPolicy(createScheduleDto);
          if (!repeatPolicy) {
            throw new Error('repeat policy is not valid');
          }
          const calendar = await this.calendarService.getCalendarByUserId(
            user.id,
          );

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

          const authority =
            await this.eventMemberService.getAuthorityId('OWNER');
          if (!authority) {
            throw new Error('authority is not valid');
          }
          await this.eventMemberService.createEventMemberBulk(
            events,
            user,
            details,
            authority,
          );
        }
      }
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

  isEqualRepeatPolicy(
    repeatPolicy: RepeatPolicy,
    createScheduleDto: CreateScheduleDto,
  ) {
    if (repeatPolicy.startDate !== createScheduleDto.startDate) {
      return false;
    }
    if (repeatPolicy.endDate !== createScheduleDto.endDate) {
      return false;
    }
    switch (createScheduleDto.repeatTerm) {
      case 'DAY':
        if (repeatPolicy.repeatDay !== createScheduleDto.repeatFrequency) {
          return false;
        }
        break;
      case 'WEEK':
        if (repeatPolicy.repeatWeek !== createScheduleDto.repeatFrequency) {
          return false;
        }
        break;
      case 'MONTH':
        if (repeatPolicy.repeatMonth !== createScheduleDto.repeatFrequency) {
          return false;
        }
        break;
      case 'YEAR':
        if (repeatPolicy.repeatYear !== createScheduleDto.repeatFrequency) {
          return false;
        }
        break;
    }
    return true;
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

  incrementDate(
    date: Date,
    repeatTerm: RepeatTerm,
    repeatFrequency: number,
  ): Date {
    const newDate = new Date(date); // 원본을 수정하지 않기 위해 새로운 Date 객체 생성
    switch (repeatTerm) {
      case RepeatTerm.DAY:
        newDate.setDate(date.getDate() + repeatFrequency);
        break;
      case RepeatTerm.WEEK:
        newDate.setDate(date.getDate() + 7 * repeatFrequency);
        break;
      case RepeatTerm.MONTH:
        newDate.setMonth(date.getMonth() + repeatFrequency);
        break;
      case RepeatTerm.YEAR:
        newDate.setFullYear(date.getFullYear() + repeatFrequency);
        break;
    }
    return newDate;
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

    let startDateCursor = new Date(createScheduleDto.startDate);
    let endDateCursor = new Date(createScheduleDto.endDate);
    const events = [];

    while (endDateCursor < endDate) {
      const event = this.eventRepository.create({
        ...createScheduleDto,
        calendar,
        repeatPolicy,
        startDate: new Date(startDateCursor), // 새로운 Date 객체 생성
        endDate: new Date(endDateCursor),
      });
      events.push(event);
      if (
        createScheduleDto.repeatTerm &&
        createScheduleDto.repeatFrequency != null
      ) {
        startDateCursor = this.incrementDate(
          startDateCursor,
          createScheduleDto.repeatTerm,
          createScheduleDto.repeatFrequency,
        );
        endDateCursor = this.incrementDate(
          endDateCursor,
          createScheduleDto.repeatTerm,
          createScheduleDto.repeatFrequency,
        );
      }
    }
    return await this.eventRepository.save(events);
  }
}
