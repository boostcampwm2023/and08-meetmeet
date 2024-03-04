import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { FindOperator, In, Raw, Repository } from 'typeorm';
import { Event } from './entities/event.entity';
import { User } from '../user/entities/user.entity';
import { CreateScheduleDto, RepeatTerm } from './dto/createSchedule.dto';
import { CalendarService } from '../calendar/calendar.service';
import { RepeatPolicy, RepeatType } from './entities/repeatPolicy.entity';
import { DetailService } from '../detail/detail.service';
import { EventMemberService } from '../event-member/event-member.service';
import { Calendar } from '../calendar/entities/calendar.entity';
import { SearchEventDto } from './dto/searchEvent.dto';
import { FollowService } from '../follow/follow.service';
import { EventsResponseDto } from './dto/events-response.dto';
import { EventResponseDto } from './dto/event-response.dto';
import { EventStoryResponseDto } from './dto/event-story-response.dto';
import { Detail } from '../detail/entities/detail.entity';
import { UserService } from '../user/user.service';
import { InviteService } from '../invite/invite.service';
import { StatusEnum } from '../invite/entities/status.enum';
import { SearchResponseDto } from './dto/search-response.dto';
import {
  AlreadyJoinedException,
  createEventFailException,
  deleteEventFailException,
  EventForbiddenException,
  EventNotFoundException,
  EventPropertyNotFoundException,
  ExpiredInviteException,
  InvalidAuthorityException,
  InvalidRepeatPolicyException,
  InviteNotFoundException,
  InviteSelfException,
  NotEventMemberException,
  NotInviteReceiverException,
  NotJoinEventException,
  NotRepeatEventException,
  SearchPeriodException,
  SearchSelfException,
  UpdateAllRepeatEventsException,
  updateEventFailException,
} from './exception/event.exception';
import { UserNotFoundException } from 'src/user/exception/user.exception';
import { EventMember } from 'src/event-member/entities/eventMember.entity';
import { FeedService } from '../feed/feed.service';
import { CreateEventResponse } from './dto/create-event-response.dto';
import { logger } from '../common/log/winston.logger';

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
    private followService: FollowService,
    private userService: UserService,
    private inviteService: InviteService,
    private feedService: FeedService,
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

    const result: EventsResponseDto[] = events.map((event) => {
      const event_detail = event.eventMembers.find(
        (eventMember) => eventMember.user.id === user.id,
      );

      if (!event_detail) {
        throw new NotEventMemberException();
      }

      return EventsResponseDto.of(event, event_detail);
    });

    return { events: result };
  }

  async getEvent(user: User, eventId: number) {
    const event = await this.eventRepository.findOne({
      where: { id: eventId },
      relations: ['repeatPolicy', 'eventMembers'],
    });

    if (!event) {
      throw new EventNotFoundException();
    }

    const authority = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    )?.authority?.displayName;

    if (!authority || !['OWNER', 'MEMBER', 'ADMIN'].includes(authority)) {
      throw new NotEventMemberException();
    }

    const detail = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    );

    if (!detail) {
      throw new NotEventMemberException();
    }

    return {
      result: EventResponseDto.of(event, detail),
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
        color:
          event.eventMembers.find(
            (eventMember) => eventMember.user.id === userId,
          )?.detail.color || null,
        alarmMinutes:
          event.eventMembers.find(
            (eventMember) => eventMember.user.id === userId,
          )?.detail.alarmMinutes || null,
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
      throw new EventNotFoundException();
    }

    return EventStoryResponseDto.of(event, user.id);
  }

  async createEvent(user: User, createScheduleDto: CreateScheduleDto) {
    // todo: calendar 어떻게 처리할지 의논
    const calendar = await this.calendarService.getCalendarByUserId(user.id);
    const authority = await this.eventMemberService.getAuthorityId('OWNER');
    if (!authority) {
      throw new createEventFailException();
    }

    let savedEventMembers: EventMember[];

    if (this.isRepeatPolicy(createScheduleDto)) {
      // TODO: transaction
      const repeatPolicy = await this.createRepeatPolicy(createScheduleDto);
      if (!repeatPolicy) {
        throw new InvalidRepeatPolicyException();
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
      if (events.length !== details.length) {
        throw new createEventFailException();
      }

      savedEventMembers = await this.eventMemberService.createEventMemberBulk(
        events,
        user,
        details,
        authority,
      );
    } else {
      const eventEntity = this.eventRepository.create({
        ...createScheduleDto,
        calendar,
      });
      const event = await this.eventRepository.save(eventEntity);
      const detail = await this.detailService.createDetail(createScheduleDto);

      savedEventMembers = [
        await this.eventMemberService.createEventMember(
          event,
          user,
          detail,
          authority,
        ),
      ];
    }

    return {
      events: savedEventMembers.map((eventMember) =>
        CreateEventResponse.of(eventMember),
      ),
    };
  }

  async deleteEvent(user: User, eventId: number, isAll: boolean) {
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers', 'feeds'],
      where: {
        id: eventId,
        eventMembers: { user: { id: user.id } },
      },
    });

    if (!event) {
      throw new EventNotFoundException();
    }
    const isValidUser = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    );
    if (!isValidUser) {
      throw new NotEventMemberException();
    }

    const authority = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    )?.authority?.displayName;

    if (!authority || !['OWNER', 'MEMBER'].includes(authority)) {
      throw new EventForbiddenException();
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
            profile: eventMember.user.profile?.path ?? null,
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

    // Q) 이건 어떤 경우인가요?
    if (!resultObject) {
      throw new NotRepeatEventException();
    }

    if (!isAll) {
      if (authority === 'OWNER') {
        if (!event.repeatPolicy) {
          await this.eventRepository.softRemove(event);
          await this.feedService.deleteEventFeeds(user, event.id);
        } else {
          const eventsWithRepeatPolicy = await this.eventRepository.findBy({
            repeatPolicyId: event.repeatPolicy.id,
          });
          if (eventsWithRepeatPolicy.length === 1) {
            await this.repeatPolicyRepository.softRemove(event.repeatPolicy);
          }
          await this.eventRepository.softRemove(event);
          await this.feedService.deleteEventFeeds(user, event.id);
        }
      } else if (authority === 'MEMBER') {
        const eventMember =
          await this.eventMemberService.getEventMemberByUserIdAndEventId(
            user.id,
            eventId,
          );
        if (!eventMember) {
          throw new NotEventMemberException();
        }
        await this.eventMemberService.deleteEventMembers([eventMember]);
      }
    } else {
      if (authority === 'OWNER') {
        const eventsWithRepeatPolicyAndNoFeed = await this.eventRepository
          .createQueryBuilder('event')
          .leftJoinAndSelect('event.eventMembers', 'eventMember')
          .leftJoinAndSelect('event.repeatPolicy', 'repeatPolicy')
          .leftJoinAndSelect('eventMember.detail', 'detail')
          .leftJoinAndSelect('eventMember.user', 'user')
          .leftJoin('Feed', 'feed', 'feed.event = event.id')
          .where('event.repeatPolicy IS NOT NULL')
          .where('event.startDate >= :startDate', {
            startDate: event.startDate,
          })
          .andWhere('feed.event IS NULL')
          .andWhere('repeatPolicy.id = :repeatPolicyId', {
            repeatPolicyId: event.repeatPolicyId,
          })
          .getMany();

        await this.eventRepository.softRemove(eventsWithRepeatPolicyAndNoFeed);
      } else if (authority === 'MEMBER') {
        const events = await this.eventRepository.find({
          relations: ['eventMembers'],
          where: {
            startDate: Raw((alias) => `${alias} >= :start`, {
              start: event.startDate,
            }),
            eventMembers: { user: { id: user.id } },
            repeatPolicy: { id: event.repeatPolicy.id },
          },
        });
        const eventMembers = events.map((event) =>
          event.eventMembers.find(
            (eventMember) => eventMember.user.id === user.id,
          ),
        );
        await Promise.all(
          eventMembers.map(async (eventMember) => {
            try {
              if (eventMember) {
                await this.eventMemberService.deleteEventMembers([eventMember]);
              }
            } catch (err) {
              logger.error(
                JSON.stringify({
                  message: err.message,
                  userId: user.id,
                  eventId: event.id,
                  eventMemberId: eventMember?.id,
                  url: '/event/deleteEvent',
                }),
              );
            }
          }),
        );
      }
    }
  }

  async updateEvent(
    user: User,
    eventId: number,
    updateScheduleDto: CreateScheduleDto,
    isAll: boolean,
  ) {
    // TODO: start가 end보다 큰 경우 예외처리
    // join 9번 select 쿼리가 2번 발생
    const prevEvent = await this.eventRepository.findOne({
      relations: ['eventMembers', 'repeatPolicy'],
      where: {
        id: eventId,
        eventMembers: { user: { id: user.id } },
      },
    });
    if (!prevEvent) {
      throw new EventNotFoundException();
    }

    const eventMember = prevEvent.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    );
    if (!eventMember) {
      throw new NotEventMemberException();
    }

    if (eventMember.authority.displayName === 'MEMBER') {
      await this.detailService.updateDetail(
        eventMember.detail,
        updateScheduleDto,
      );
    } else if (eventMember.authority.displayName === 'OWNER') {
      if (!isAll) {
        return await this.updateOneEvent(
          user,
          prevEvent,
          eventMember,
          updateScheduleDto,
        );
      }
      return await this.updateAllRepeatEvents(
        user,
        prevEvent,
        eventMember,
        updateScheduleDto,
      );
    }
  }

  async updateAllRepeatEvents(
    user: User,
    prevEvent: Event,
    eventMember: EventMember,
    updateScheduleDto: CreateScheduleDto,
  ) {
    if (!prevEvent.repeatPolicy) {
      throw new NotRepeatEventException();
    }

    const eventMembersToUpdate =
      await this.eventMemberService.getEventMembersByStartDateAndRepeatPolicy(
        user.id,
        prevEvent.startDate,
        prevEvent.repeatPolicy.id,
      );
    const newEvent: Partial<Event> = this.eventRepository.create({
      title: updateScheduleDto.title,
      isJoinable: updateScheduleDto.isJoinable,
    });
    const eventIds = eventMembersToUpdate.map((em) => {
      if (!em.event) {
        throw new updateEventFailException();
      }
      return em.event.id;
    });

    if (
      this.isRepeatPolicy(updateScheduleDto) &&
      this.isEqualRepeatPolicy(prevEvent, updateScheduleDto)
    ) {
      await this.detailService.updateDetailBulk(
        eventMembersToUpdate.map((em) => {
          if (!em.detail) {
            throw new updateEventFailException();
          }
          return em.detail.id;
        }),
        updateScheduleDto,
      );

      await this.eventRepository.update(eventIds, newEvent);
      return;
    }

    const eventsWithNoFeed = await this.eventRepository.find({
      relations: ['feeds'],
      where: {
        feeds: [],
        id: In(eventIds.filter((id) => id !== prevEvent.id)),
      },
    });

    if (eventsWithNoFeed.length) {
      const noFeedEventIds = eventsWithNoFeed.map((v) => v.id);
      if (!noFeedEventIds.length) {
        throw new deleteEventFailException();
      }
      await this.eventRepository.softDelete(noFeedEventIds);
      await this.eventMemberService.deleteEventMembersByEventIds(
        noFeedEventIds,
      );
    }

    await this.updatePrevAndCreateNewEvents(
      user,
      prevEvent,
      eventMember,
      updateScheduleDto,
    );
    await this.deleteRepeatPolicyIfNoEvent(prevEvent.repeatPolicy.id);
  }

  async updateOneEvent(
    user: User,
    prevEvent: Event,
    eventMember: EventMember,
    updateScheduleDto: CreateScheduleDto,
  ) {
    if (!prevEvent.repeatPolicyId) {
      return await this.updatePrevAndCreateNewEvents(
        user,
        prevEvent,
        eventMember,
        updateScheduleDto,
      );
    }
    if (this.isRepeatPolicy(updateScheduleDto)) {
      if (!this.isEqualRepeatPolicy(prevEvent, updateScheduleDto)) {
        throw new UpdateAllRepeatEventsException();
      }
    }

    await this.detailService.updateDetail(
      eventMember.detail,
      updateScheduleDto,
    );

    const newEvent = this.eventRepository.create({
      id: prevEvent.id,
      calendar: prevEvent.calendar,
      ...updateScheduleDto,
      repeatPolicyId: null,
    });

    await this.eventRepository.save(newEvent);
    await this.deleteRepeatPolicyIfNoEvent(prevEvent.repeatPolicyId);
  }

  async updatePrevAndCreateNewEvents(
    user: User,
    prevEvent: Event,
    eventMember: EventMember,
    updateScheduleDto: CreateScheduleDto,
  ) {
    await this.detailService.updateDetail(
      eventMember.detail,
      updateScheduleDto,
    );

    const newEvent = this.eventRepository.create({
      id: prevEvent.id,
      calendar: prevEvent.calendar,
      ...updateScheduleDto,
      repeatPolicyId: null,
    });

    await this.eventRepository.save(newEvent);

    if (this.isRepeatPolicy(updateScheduleDto)) {
      const nextRepeatEventDto: CreateScheduleDto = {
        ...updateScheduleDto,
        startDate: this.incrementDate(
          newEvent.startDate,
          updateScheduleDto.repeatTerm!,
          updateScheduleDto.repeatFrequency!,
        ),
        endDate: this.incrementDate(
          newEvent.endDate,
          updateScheduleDto.repeatTerm!,
          updateScheduleDto.repeatFrequency!,
        ),
      };
      const nextEvents = await this.createEvent(user, nextRepeatEventDto);
      await this.eventRepository.update(newEvent.id, {
        repeatPolicyId: nextEvents.events.at(0)?.repeatPolicyId ?? null,
      });
    }
  }

  async updateEventAnnouncement(
    user: User,
    eventId: number,
    announcement: string,
  ) {
    const authority = await this.eventMemberService.getAuthorityOfUserByEventId(
      eventId,
      user.id,
    );

    if (!authority || authority !== 'OWNER') {
      throw new EventForbiddenException();
    }

    await this.eventRepository.update(eventId, {
      announcement: announcement ?? null,
    });
  }

  async searchEvent(user: User, searchEventDto: SearchEventDto) {
    const startDate = new Date(searchEventDto.startDate);
    const endDate = new Date(searchEventDto.endDate);

    const sixMonthsInMillis = 6 * 30 * 24 * 60 * 60 * 1000;

    if (endDate.getTime() - startDate.getTime() > sixMonthsInMillis) {
      throw new SearchPeriodException();
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
      const event_detail = event.eventMembers.find(
        (eventMember) => eventMember.user.id === user.id,
      );

      if (!event_detail) {
        throw new NotEventMemberException();
      }

      return EventsResponseDto.of(event, event_detail);
    });

    if (!result) {
      return { events: [] };
    }
    return { events: result };
  }

  isValidRepeatPolicy(createScheduleDto: CreateScheduleDto) {
    const { repeatTerm, repeatFrequency, repeatEndDate } = createScheduleDto;
    if (!repeatTerm || !repeatFrequency) {
      return false;
    }
    if (repeatFrequency <= 0 || repeatFrequency > 7) {
      return false;
    }
    if (!repeatEndDate) {
      createScheduleDto.repeatEndDate = new Date('2038-01-18');
    }
    return true;
  }

  isRepeatPolicy(createScheduleDto: CreateScheduleDto) {
    if (!createScheduleDto.repeatTerm) {
      return false;
    }
    const isValid = this.isValidRepeatPolicy(createScheduleDto);
    if (!isValid) {
      throw new InvalidRepeatPolicyException();
    }
    return true;
  }

  isEqualRepeatPolicy(prevEvent: Event, createScheduleDto: CreateScheduleDto) {
    const { repeatEndDate, repeatTerm, repeatFrequency } = createScheduleDto;

    if (
      prevEvent.repeatPolicy.endDate.getDate() !==
      new Date(repeatEndDate!).getDate()
    ) {
      return false;
    }
    if (
      !this.isEqualDate(prevEvent.startDate, createScheduleDto.startDate) ||
      !this.isEqualDate(prevEvent.endDate, createScheduleDto.endDate)
    ) {
      return false;
    }

    return (
      prevEvent.repeatPolicy[
        RepeatType[repeatTerm as keyof typeof RepeatType]
      ] === repeatFrequency!
    );
  }

  async deleteRepeatPolicyIfNoEvent(id: number) {
    if (
      !(await this.eventRepository.find({ where: { repeatPolicyId: id } }))
        .length
    ) {
      await this.repeatPolicyRepository.softDelete(id);
    }
  }

  async createRepeatPolicy(createScheduleDto: CreateScheduleDto) {
    const { startDate, repeatEndDate, repeatTerm, repeatFrequency } =
      createScheduleDto;
    const repeatPolicy = this.repeatPolicyRepository.create({
      startDate: startDate,
      endDate: repeatEndDate,
    });
    if (!repeatFrequency) {
      throw new InvalidRepeatPolicyException();
    }

    repeatPolicy[RepeatType[repeatTerm as keyof typeof RepeatType]] =
      repeatFrequency;

    return await this.repeatPolicyRepository.save(repeatPolicy);
  }

  isEqualDate(first: Date, second: Date) {
    return new Date(first).getTime() === new Date(second).getTime();
  }

  incrementDate(
    date: Date,
    repeatTerm: RepeatTerm,
    repeatFrequency: number,
  ): Date {
    const newDate = new Date(date); // 원본을 수정하지 않기 위해 새로운 Date 객체 생성
    switch (repeatTerm) {
      case RepeatTerm.DAY:
        newDate.setDate(newDate.getDate() + repeatFrequency);
        break;
      case RepeatTerm.WEEK:
        newDate.setDate(newDate.getDate() + 7 * repeatFrequency);
        break;
      case RepeatTerm.MONTH:
        newDate.setMonth(newDate.getMonth() + repeatFrequency);
        break;
      case RepeatTerm.YEAR:
        newDate.setFullYear(newDate.getFullYear() + repeatFrequency);
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
    const repeatEndDate = new Date(
      createScheduleDto.repeatEndDate ?? '2038-01-18',
    );
    const { repeatTerm, repeatFrequency } = createScheduleDto;
    if (!repeatTerm || !repeatFrequency) {
      throw new InvalidRepeatPolicyException();
    }

    const events: Event[] = [];
    let startDateCursor = new Date(createScheduleDto.startDate);
    let endDateCursor = new Date(createScheduleDto.endDate);

    while (endDateCursor <= repeatEndDate) {
      const event = this.eventRepository.create({
        ...createScheduleDto,
        calendar,
        repeatPolicy,
        startDate: new Date(startDateCursor), // 새로운 Date 객체 생성
        endDate: new Date(endDateCursor),
      });

      events.push(event);

      startDateCursor = this.incrementDate(
        startDateCursor,
        repeatTerm,
        repeatFrequency,
      );
      endDateCursor = this.incrementDate(
        endDateCursor,
        repeatTerm,
        repeatFrequency,
      );
    }

    const result = await this.eventRepository.insert(events);

    return await this.eventRepository.find({
      where: { id: In(result.identifiers.map((identifier) => identifier.id)) },
    });
  }

  setNextRepeatEventDates(
    newEvent: Partial<Event>,
    term: RepeatTerm,
    frequency: number,
  ) {
    if (!newEvent.startDate || !newEvent.endDate) {
      throw new EventPropertyNotFoundException();
    }

    newEvent.startDate = this.incrementDate(
      newEvent.startDate,
      term,
      frequency,
    );
    newEvent.endDate = this.incrementDate(newEvent.endDate, term, frequency);

    return newEvent;
  }

  async getFollowingsEvents(user: User, eventId: number) {
    const rawFollowings = await this.followService.getRawFollowings(user);
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers'],
      where: {
        id: eventId,
      },
    });
    if (!event) {
      throw new EventNotFoundException();
    }
    const invites = await this.inviteService.getInvitesByEvent(event);
    const result: any[] = [];
    await Promise.all(
      rawFollowings.map(async (follower) => {
        let isJoined;
        const eventMember = event.eventMembers.find(
          (eventMember) => eventMember.user.id === follower.follower.id,
        );
        const inviteByEventAndUserLimitOne =
          await this.inviteService.getInviteByEventAndUser(
            event,
            follower.follower,
          );
        if (!eventMember) {
          invites.find((invite) => {
            if (invite.receiver.id === follower.follower.id) {
              if (inviteByEventAndUserLimitOne) {
                if (
                  inviteByEventAndUserLimitOne[0].status.displayName ===
                  StatusEnum.Pending
                ) {
                  isJoined = StatusEnum.Pending;
                } else {
                  isJoined = StatusEnum.Joinable;
                }
              } else {
                isJoined = StatusEnum.Joinable;
              }
            }
          });
          if (!isJoined) {
            isJoined = StatusEnum.Joinable;
          }
        } else {
          isJoined = StatusEnum.Accepted;
        }
        result.push({
          id: follower.follower.id,
          nickname: follower.follower.nickname,
          profile: follower.follower.profile?.path ?? null,
          isJoined: isJoined,
        });
      }),
    );

    return { users: result };
  }
  async getFollowersEvents(user: User, eventId: number) {
    const rawFollowers = await this.followService.getRawFollowers(user);
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers'],
      where: {
        id: eventId,
      },
    });
    if (!event) {
      throw new EventNotFoundException();
    }
    const invites = await this.inviteService.getInvitesByEvent(event);
    const result: any[] = [];
    await Promise.all(
      rawFollowers.map(async (follower) => {
        let isJoined;
        const eventMember = event.eventMembers.find(
          (eventMember) => eventMember.user.id === follower.user.id,
        );
        const inviteByEventAndUserLimitOne =
          await this.inviteService.getInviteByEventAndUser(
            event,
            follower.user,
          );
        if (!eventMember) {
          invites.find((invite) => {
            if (invite.receiver.id === follower.user.id) {
              if (inviteByEventAndUserLimitOne) {
                if (
                  inviteByEventAndUserLimitOne[0].status.displayName ===
                  StatusEnum.Pending
                ) {
                  isJoined = StatusEnum.Pending;
                } else {
                  isJoined = StatusEnum.Joinable;
                }
              } else {
                isJoined = StatusEnum.Joinable;
              }
            }
          });
          if (!isJoined) {
            isJoined = StatusEnum.Joinable;
          }
        } else {
          isJoined = StatusEnum.Accepted;
        }
        result.push({
          id: follower.user.id,
          nickname: follower.user.nickname,
          profile: follower.user.profile?.path ?? null,
          isJoined: isJoined,
        });
      }),
    );
    return { users: result };
  }

  async searchUserEvents(user: User, nickname: string, eventId: number) {
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers'],
      where: {
        id: eventId,
      },
    });
    if (!event) {
      throw new EventNotFoundException();
    }

    const findByNickname = await this.userService.findUserByNickname(nickname);
    if (!findByNickname) {
      return SearchResponseDto.of([], [], []);
    } else {
      if (findByNickname.id === user.id) {
        throw new SearchSelfException();
      }
    }

    const eventMember = event.eventMembers.find(
      (eventMember) => eventMember.user.id === findByNickname.id,
    );
    const invites = await this.inviteService.getInvitesByEvent(event);
    const inviteByEventAndUserLimitOne =
      await this.inviteService.getInviteByEventAndUser(event, findByNickname);
    let isJoined;
    if (!eventMember) {
      invites.find((invite) => {
        if (invite.receiver.id === findByNickname.id) {
          if (inviteByEventAndUserLimitOne) {
            if (
              inviteByEventAndUserLimitOne[0].status.displayName ===
              StatusEnum.Pending
            ) {
              isJoined = StatusEnum.Pending;
            } else {
              isJoined = StatusEnum.Joinable;
            }
          } else {
            isJoined = StatusEnum.Joinable;
          }
        }
      });
      if (!isJoined) {
        isJoined = StatusEnum.Joinable;
      }
    } else {
      isJoined = StatusEnum.Accepted;
    }
    return SearchResponseDto.of([findByNickname], [], [isJoined]);
  }

  async inviteSchedule(user: User, userId: number, eventId: number) {
    const event = await this.eventRepository.findOne({
      where: { id: eventId },
    });

    if (!event) {
      throw new EventNotFoundException();
    }

    if (user.id === userId) {
      throw new InviteSelfException();
    }

    event.eventMembers.forEach((eventMember) => {
      if (eventMember.user.id === userId) {
        throw new AlreadyJoinedException();
      }
    });

    const invitedUser = await this.userService.findUserById(userId);

    if (!invitedUser) {
      throw new UserNotFoundException();
    }

    await this.inviteService.sendInviteEventMessage(user, event, invitedUser);
    return { result: '초대요청이 전송되었습니다.' };
  }

  async joinSchedule(user: User, eventId: number) {
    const event = await this.eventRepository.findOne({
      where: { id: eventId },
    });

    if (!event) {
      throw new EventNotFoundException();
    }

    if (!event.isJoinable) {
      throw new NotJoinEventException();
    }

    event.eventMembers.forEach((eventMember) => {
      if (eventMember.user.id === user.id) {
        throw new AlreadyJoinedException();
      }
    });

    const detail = {
      isVisible: true,
      memo: '',
      color: -39579,
      alarmMinutes: 10,
    } as Detail;

    const savedDetail = await this.detailService.createDetailSingle(detail);

    const authority = await this.eventMemberService.getAuthorityId('MEMBER');
    if (!authority) {
      throw new EventForbiddenException();
    }

    await this.eventMemberService.createEventMember(
      event,
      user,
      savedDetail,
      authority,
    );

    await this.inviteService.updateInviteWhenJoinEvent(user, event);
    return { result: '일정에 참가하였습니다.' };
  }

  async acceptSchedule(
    user: User,
    eventId: number,
    inviteId: number,
    isAccept: boolean,
  ) {
    const event = await this.eventRepository.findOne({
      where: { id: eventId },
    });

    if (!event) {
      throw new NotEventMemberException();
    }

    const invite = await this.inviteService.getInviteById(inviteId);

    if (!invite) {
      throw new InviteNotFoundException();
    }

    if (invite.status.displayName !== StatusEnum.Pending) {
      throw new ExpiredInviteException();
    }

    if (invite.receiver.id !== user.id) {
      throw new NotInviteReceiverException();
    }

    if (isAccept) {
      const detail = {
        isVisible: true,
        memo: '',
        color: -39579,
        alarmMinutes: 10,
      } as Detail;

      const savedDetail = await this.detailService.createDetailSingle(detail);

      const authority = await this.eventMemberService.getAuthorityId('MEMBER');
      if (!authority) {
        throw new InvalidAuthorityException();
      }

      await this.eventMemberService.createEventMember(
        event,
        user,
        savedDetail,
        authority,
      );
      await this.inviteService.updateInvite(invite.id, StatusEnum.Accepted);
      return { result: '일정에 참가하였습니다.' };
    } else {
      await this.inviteService.updateInvite(invite.id, StatusEnum.Rejected);
      return { result: '일정에 참가하지 않았습니다.' };
    }
  }
}
