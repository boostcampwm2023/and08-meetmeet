import {
  BadRequestException,
  HttpException,
  HttpStatus,
  Injectable,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { FindOperator, Raw, Repository } from 'typeorm';
import { Event } from './entities/event.entity';
import { User } from '../user/entities/user.entity';
import { CreateScheduleDto, RepeatTerm } from './dto/createSchedule.dto';
import { CalendarService } from '../calendar/calendar.service';
import { RepeatPolicy } from './entities/repeatPolicy.entity';
import { DetailService } from '../detail/detail.service';
import { EventMemberService } from '../event-member/event-member.service';
import { Calendar } from '../calendar/entities/calendar.entity';
import { SearchEventDto } from './dto/searchEvent.dto';
import { UpdateScheduleDto } from './dto/updateSchedule.dto';

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
    const events = await this.eventRepository.find({
      relations: ['eventMembers'],
      where: {
        startDate: Raw((alias) => `${alias} <= :end`, {
          end: endDate,
        }),
        endDate: Raw((alias) => `${alias} >= :start`, {
          start: startDate,
        }),
        eventMembers: { user: { id: user.id } },
      },
    });

    const result: any[] = [];
    events.forEach((event) => [
      result.push({
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
        isJoinable: event.isJoinable ? true : false,
      }),
    ]);
    return { events: result };
  }

  async getEvent(user: User, eventId: number) {
    const event = await this.eventRepository.findOne({
      where: { id: eventId },
      relations: ['repeatPolicy', 'eventMembers'],
    });
    console.log(event);
    if (!event) {
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    if (
      event.eventMembers.find((eventMember) => eventMember.user.id === user.id)
        ?.authority?.displayName !== 'OWNER' &&
      event.eventMembers.find((eventMember) => eventMember.user.id === user.id)
        ?.authority?.displayName !== 'ADMIN' &&
      event.eventMembers.find((eventMember) => eventMember.user.id === user.id)
        ?.authority?.displayName !== 'MEMBER'
    ) {
      throw new HttpException(
        '이벤트에 참여하지 않았습니다.',
        HttpStatus.NOT_FOUND,
      );
    }

    const detail = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    )?.detail;

    const repeatPolicy = event.repeatPolicy;

    let repeatTerm;
    let repeatFrequency;
    let repeatEndDate;
    if (repeatPolicy?.repeatDay !== undefined) {
      repeatTerm = 'DAY';
      repeatFrequency = repeatPolicy.repeatDay;
      repeatEndDate = repeatPolicy.repeatDay;
    } else if (repeatPolicy?.repeatWeek !== undefined) {
      repeatTerm = 'WEEK';
      repeatFrequency = repeatPolicy.repeatWeek;
      repeatEndDate = repeatPolicy.repeatDay;
    } else if (repeatPolicy?.repeatMonth !== undefined) {
      repeatTerm = 'MONTH';
      repeatFrequency = repeatPolicy.repeatMonth;
      repeatEndDate = repeatPolicy.repeatDay;
    } else if (repeatPolicy?.repeatYear !== undefined) {
      repeatTerm = 'YEAR';
      repeatFrequency = repeatPolicy.repeatYear;
      repeatEndDate = repeatPolicy.repeatDay;
    } else {
      repeatTerm = null;
      repeatFrequency = null;
      repeatEndDate = null;
    }

    return {
      result: {
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
        isJoinable: event.isJoinable ? true : false,
        isVisible: detail?.isVisible ? true : false,
        memo: detail?.memo,
        repeatTerm: repeatTerm,
        repeatFrequency: repeatFrequency,
        repeatEndDate: repeatEndDate,
      },
    };
  }

  async getUserEvents(
    user: User,
    userId: number,
    startDate: string,
    endDate: string,
  ) {
    const events = await this.eventRepository.find({
      relations: ['eventMembers'],
      where: {
        startDate: Raw((alias) => `${alias} <= :end`, {
          end: endDate,
        }),
        endDate: Raw((alias) => `${alias} >= :start`, {
          start: startDate,
        }),
        eventMembers: { user: { id: userId }, detail: { isVisible: true } },
      },
    });
    const result: any[] = [];
    events.forEach((event) => [
      result.push({
        id: event.id,
        title: event.title,
        startDate: event.startDate,
        endDate: event.endDate,
        // color: event.eventMembers.find((eventMember) => eventMember.user.id === userId).detail || null,
        color: null,
      }),
    ]);
    return { events: result };
  }

  async getEventFeeds(user: User, eventId: number) {
    const event = await this.eventRepository.findOne({
      where: { id: eventId },
      relations: [
        'repeatPolicy',
        'eventMembers',
        'feeds',
        'feeds.feedContents',
        'feeds.feedContents.content',
      ],
    });
    if (!event) {
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    return {
      id: event.id,
      title: event.title,
      startDate: event.startDate,
      endDate: event.endDate,
      announcement: event.announcement,
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
      isJoinable: event.isJoinable ? true : false,
      isVisible: event.eventMembers.find(
        (eventMember) => eventMember.user.id === user.id,
      )?.detail?.isVisible
        ? true
        : false,
      memo: event.eventMembers.find(
        (eventMember) => eventMember.user.id === user.id,
      )?.detail?.memo,
      feeds: event.feeds.map((feed) => ({
        id: feed.id,
        thumbnail: feed.feedContents[0]?.content?.thumbnail,
        memo: feed.memo,
      })),
    };
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
    // 일정 중간부터면 생각 다시해야한다....
    // 단건 삭제
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers', 'feeds'],
      where: {
        id: eventId,
        eventMembers: { user: { id: user.id } },
      },
    });
    // if (
    //   event?.eventMembers.some(
    //     (eventMember) =>
    //       eventMember.user.id === user.id &&
    //       eventMember.authority.displayName !== 'OWNER',
    //   )
    // ) {
    //   throw UnauthorizedException;
    // }

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
      if (updatedEvent.authority === 'OWNER') {
        await this.eventRepository.softRemove(event);
      } else if (updatedEvent.authority === 'ADMIN') {
        // todo 운영자는 어떻게 할지 고민
      } else if (updatedEvent.authority === 'MEMBER') {
        await this.eventMemberService.deleteEventMemberByEventId(event);
        // for (const eventMember of event.eventMembers) {
        //   if (eventMember.user.id === user.id) {
        //     await this.detailService.deleteDetail(eventMember.detail);
        //   }
        // }
      }
    } else {
      // 전체 삭제를 가정(반복인걸 이미 가정되고 들어와야한다.)
      // 반복일정인지 체크는 해야하지 않나...
      // 주인인지 아닌지 판단하고 전체 삭제해야한다.
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
        throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
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
            isJoinable: event.isJoinable ? true : false,
            detail: event.eventMembers[0].detail,
          }
        : null;

      if (!resultObject) {
        throw new HttpException(
          '반복되는 컨텐츠가 아닙니다.',
          HttpStatus.NOT_FOUND,
        );
      }

      const eventsWithRepeatPolicyAndNoFeed = await this.eventRepository
        .createQueryBuilder('event')
        .leftJoinAndSelect('event.eventMembers', 'eventMember')
        .leftJoinAndSelect('event.repeatPolicy', 'repeatPolicy')
        .leftJoinAndSelect('eventMember.detail', 'detail')
        .leftJoinAndSelect('eventMember.user', 'user')
        .leftJoin('Feed', 'feed', 'feed.event = event.id')
        .where('event.repeatPolicy IS NOT NULL')
        .andWhere('feed.event IS NULL')
        .andWhere('repeatPolicy.id = :repeatPolicyId', {
          repeatPolicyId: resultObject.repeatPolicy.repeatPolicyId,
        })
        .getMany();
      // 삭제하려는게 전체 반복의 중간일 경우(?)
      for (const event1 of eventsWithRepeatPolicyAndNoFeed) {
        // todo 일괄 처리되도록 수정해야한다.

        for (const eventMember of event1.eventMembers) {
          if (eventMember.user.id === user.id) {
            await this.detailService.deleteDetail(eventMember.detail);
          }
        }
        await this.eventRepository.softRemove(event1);
        await this.eventMemberService.deleteEventMemberByEventId(event1);

        await this.repeatPolicyRepository.softRemove(
          resultObject.repeatPolicy.repeatPolicyName,
        );
      }
    }
  }

  async updateEvent(
    user: User,
    eventId: number,
    updateScheduleDto: UpdateScheduleDto,
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
      throw new HttpException('컨텐츠가 없습니다.', HttpStatus.NOT_FOUND);
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
          isJoinable: event.isJoinable ? true : false,
          detail: event.eventMembers.find(
            (eventMember) => eventMember.user.id === user.id,
          )?.detail,
        }
      : null;

    if (!resultObject) {
      throw new HttpException('컨텐츠가 없습니다.', HttpStatus.NOT_FOUND);
    }
    const detail = resultObject.detail;
    if (!detail) {
      throw new Error('detail is not valid');
    }

    if (resultObject.authority === 'MEMBER') {
      await this.detailService.updateDetail(detail, updateScheduleDto);
    } else if (resultObject.authority === 'OWNER') {
      // if (resultObject.repeatPolicy.repeatPolicyId === null) {
      // todo 이 부분에서 에러날 것 같습니다.
      if (!isAll) {
        // 주인이고 반복일정이 아닌경우
        await this.detailService.updateDetail(detail, updateScheduleDto);
        await this.eventRepository.save({
          ...event,
          ...updateScheduleDto,
        });
      } else {
        // 주인이고 반복일정인 경우
        if (
          this.isEqualRepeatPolicy(
            resultObject.repeatPolicy.repeatPolicyName,
            updateScheduleDto,
          )
        ) {
          // 반복일정이 이전과 같은경우
          await this.detailService.updateDetail(detail, updateScheduleDto);
          await this.eventRepository.save({
            ...event,
            ...updateScheduleDto,
          });
        } else {
          // 반복일정이 같지 않은 경우 -> 반복일정을 삭제하고 새로 생성(피드가 없는경우)
          const eventsWithRepeatPolicyAndNoFeed = await this.eventRepository
            .createQueryBuilder('event')
            .leftJoinAndSelect('event.eventMembers', 'eventMember')
            .leftJoinAndSelect('event.repeatPolicy', 'repeatPolicy')
            .leftJoinAndSelect('eventMember.detail', 'detail')
            .leftJoinAndSelect('eventMember.user', 'user')
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

            for (const eventMember of event1.eventMembers) {
              if (eventMember.user.id === user.id) {
                await this.detailService.deleteDetail(eventMember.detail);
              }
            }
          }

          await this.repeatPolicyRepository.softRemove(
            resultObject.repeatPolicy.repeatPolicyName,
          );
          const repeatPolicy = await this.createRepeatPolicy(updateScheduleDto);
          if (!repeatPolicy) {
            throw new Error('repeat policy is not valid');
          }
          const calendar = await this.calendarService.getCalendarByUserId(
            user.id,
          );

          const events = await this.createRepeatEvent(
            user,
            updateScheduleDto,
            calendar,
            repeatPolicy,
          );
          const details = await this.detailService.createDetailBulk(
            updateScheduleDto,
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

  async searchEvent(user: User, searchEventDto: SearchEventDto) {
    const startDate = new Date(searchEventDto.startDate);
    const endDate = new Date(searchEventDto.endDate);

    const sixMonthsInMillis = 6 * 30 * 24 * 60 * 60 * 1000;

    if (endDate.getTime() - startDate.getTime() > sixMonthsInMillis) {
      throw new BadRequestException('일정 검색 기간은 6개월 이내여야 합니다.');
    }

    const whereClause: {
      startDate: FindOperator<any>;
      endDate: FindOperator<any>;
      title?: FindOperator<any>;
      eventMembers: { user: { id: number } };
    } = {
      startDate: Raw((alias) => `${alias} <= :end`, {
        end: endDate,
      }),
      endDate: Raw((alias) => `${alias} >= :start`, {
        start: startDate,
      }),
      eventMembers: { user: { id: user.id } },
    };

    if (searchEventDto.keyword) {
      whereClause.title = Raw((alias) => `${alias} LIKE :title`, {
        title: `%${searchEventDto.keyword}%`,
      });
    }
    const events = await this.eventRepository.find({
      relations: ['eventMembers'],
      where: whereClause,
    });

    const result = events.map((event) => {
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
        isJoinable: event.isJoinable ? true : false,
      };
    });

    if (!result) {
      return { events: [] };
    }
    return { events: result };
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
