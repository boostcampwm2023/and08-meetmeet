import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { FindOperator, In, Raw, Repository } from 'typeorm';
import { Event } from './entities/event.entity';
import { User } from '../user/entities/user.entity';
import { CreateScheduleDto, RepeatTerm } from './dto/createSchedule.dto';
import { CalendarService } from '../calendar/calendar.service';
import { RepeatType, RepeatPolicy } from './entities/repeatPolicy.entity';
import { DetailService } from '../detail/detail.service';
import { EventMemberService } from '../event-member/event-member.service';
import { Calendar } from '../calendar/entities/calendar.entity';
import { SearchEventDto } from './dto/searchEvent.dto';
import { UpdateScheduleDto } from './dto/updateSchedule.dto';
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
  EventForbiddenException,
  EventNotFoundException,
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
} from './exception/event.exception';
import { UserNotFoundException } from 'src/user/exception/user.exception';
import { EventMember } from 'src/event-member/entities/eventMember.entity';
import { FeedService } from '../feed/feed.service';

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

    if (this.isReapeatPolicy(createScheduleDto)) {
      if (!this.isReapeatPolicyValid(createScheduleDto)) {
        throw new InvalidRepeatPolicyException();
      }

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

    const results = savedEventMembers.map((eventMember) => {
      return {
        id: eventMember.event.id,
        startDate: eventMember.event.startDate,
        endDate: eventMember.event.endDate,
        title: eventMember.event.title,
        repeatPolicyId: eventMember.event.repeatPolicyId,
        isJoinable: eventMember.event.isJoinable ? true : false,
        announcement: eventMember.event.announcement,
        isVisible: eventMember.detail.isVisible ? true : false,
        memo: eventMember.detail.memo,
        color: eventMember.detail.color,
        alarmMinutes: eventMember.detail.alarmMinutes,
        authority: eventMember.authority.displayName,
      };
    });

    return { events: results };
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
            repeatPolicyId: event.repeatPolicyId,
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
        await this.eventMemberService.deleteEventMemberByEventMemberId(
          eventMember.id,
        );
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
            repeatPolicy: { id: event.repeatPolicyId },
          },
        });
        const eventMembersIds = events.map(
          (event) =>
            event.eventMembers.find(
              (eventMember) => eventMember.user.id === user.id,
            )?.id,
        );
        await Promise.all(
          eventMembersIds.map(async (eventMemberId) => {
            try {
              if (eventMemberId) {
                await this.eventMemberService.deleteEventMemberByEventMemberId(
                  eventMemberId,
                );
              }
            } catch (err) {
              // todo 어떻게 에러 처리할지
              console.log(err);
            }
          }),
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
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers', 'repeatPolicy'],
      where: {
        id: eventId,
        eventMembers: { user: { id: user.id } },
      },
    });

    if (!event) {
      throw new EventNotFoundException();
    }

    const eventMember = event.eventMembers.find(
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
        if (!event.repeatPolicy) {
          await this.detailService.updateDetail(
            eventMember.detail,
            updateScheduleDto,
          );
          const newEvent = await this.eventRepository.create({
            ...event,
            ...updateScheduleDto,
          });
          await this.eventRepository.save(newEvent);
          return newEvent;
        } else {
          const eventsWithRepeatPolicy = await this.eventRepository.findBy({
            repeatPolicyId: event.repeatPolicyId,
          });

          if (eventsWithRepeatPolicy.length === 1) {
            await this.repeatPolicyRepository.softRemove(event.repeatPolicy);
          }

          if (!this.isReapeatPolicy(updateScheduleDto)) {
            await this.detailService.updateDetail(
              eventMember.detail,
              updateScheduleDto,
            );
            const newEvent = await this.eventRepository.create({
              ...event,
              ...updateScheduleDto,
            });
            await this.eventRepository.save(newEvent);
            return newEvent;
          } else {
            if (
              this.isEqualRepeatPolicy(event.repeatPolicy, updateScheduleDto)
            ) {
              await this.detailService.updateDetail(
                eventMember.detail,
                updateScheduleDto,
              );
              const newEvent = await this.eventRepository.create({
                ...event,
                ...updateScheduleDto,
              });
              await this.eventRepository.save(newEvent);
              return newEvent;
            } else {
              await this.eventRepository.softRemove(event);

              const repeatPolicy =
                await this.createRepeatPolicy(updateScheduleDto);

              if (!repeatPolicy) {
                throw new InvalidRepeatPolicyException();
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
                throw new EventForbiddenException();
              }

              const savedEventMembers =
                await this.eventMemberService.createEventMemberBulk(
                  events,
                  user,
                  details,
                  authority,
                );

              const results: any[] = [];
              savedEventMembers.forEach((eventMember) => {
                results.push({
                  id: eventMember.event.id,
                  startDate: eventMember.event.startDate,
                  endDate: eventMember.event.endDate,
                  title: eventMember.event.title,
                  repeatPolicyId: eventMember.event.repeatPolicyId,
                  isJoinable: eventMember.event.isJoinable ? true : false,
                  announcement: eventMember.event.announcement,
                  isVisible: eventMember.detail.isVisible ? true : false,
                  memo: eventMember.detail.memo,
                  color: eventMember.detail.color,
                  alarmMinutes: eventMember.detail.alarmMinutes,
                  authority: eventMember.authority.displayName,
                });
              });
              return { events: results };
            }
          }
        }
      } else {
        if (this.isEqualRepeatPolicy(event.repeatPolicy, updateScheduleDto)) {
          const eventsWithRepeatPolicy = await this.eventRepository.findBy({
            repeatPolicyId: event.repeatPolicyId,
          });

          const detailIds: number[] = [];
          eventsWithRepeatPolicy.forEach((event) => {
            event.eventMembers.forEach((eventMember) => {
              if (eventMember.user.id === user.id) {
                detailIds.push(eventMember.detail.id);
              }
            });
          });

          const detail = {
            ...eventMember.detail,
            ...updateScheduleDto,
          };
          await this.detailService.bulkUpdateDetail(detailIds, detail);
          const newEvent = await this.eventRepository.create({
            ...event,
            ...updateScheduleDto,
          });
          await this.eventRepository.save(newEvent);
          return newEvent;
        } else {
          const eventsWithRepeatPolicyAndFeed = await this.eventRepository.find(
            {
              relations: ['eventMembers', 'repeatPolicy', 'feeds'],
              where: {
                repeatPolicyId: event.repeatPolicyId,
                feeds: { eventId: Raw((alias) => `${alias} IS NULL`) },
              },
            },
          );

          await this.eventRepository.softRemove(eventsWithRepeatPolicyAndFeed);
          await this.repeatPolicyRepository.softRemove(event.repeatPolicy);
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
          const savedEventMembers =
            await this.eventMemberService.createEventMemberBulk(
              events,
              user,
              details,
              authority,
            );

          const results: any[] = [];
          savedEventMembers.forEach((eventMember) => {
            results.push({
              id: eventMember.event.id,
              startDate: eventMember.event.startDate,
              endDate: eventMember.event.endDate,
              title: eventMember.event.title,
              repeatPolicyId: eventMember.event.repeatPolicyId,
              isJoinable: eventMember.event.isJoinable ? true : false,
              announcement: eventMember.event.announcement,
              isVisible: eventMember.detail.isVisible ? true : false,
              memo: eventMember.detail.memo,
              color: eventMember.detail.color,
              alarmMinutes: eventMember.detail.alarmMinutes,
              authority: eventMember.authority.displayName,
            });
          });
          return { events: results };
        }
      }
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

  isReapeatPolicyValid(createScheduleDto: CreateScheduleDto) {
    const { repeatTerm, repeatFrequency, repeatEndDate } = createScheduleDto;
    if (!repeatTerm || !repeatFrequency) {
      return false;
    }
    // Q) repeatTerm이 DAY일 때만 적용되는 걸까요
    if (repeatFrequency <= 0 || repeatFrequency > 7) {
      return false;
    }
    if (!repeatEndDate) {
      createScheduleDto.repeatEndDate = new Date('2038-01-18');
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
    const { startDate, repeatEndDate, repeatTerm, repeatFrequency } =
      createScheduleDto;

    if (
      repeatPolicy.startDate !== startDate ||
      repeatPolicy.endDate !== repeatEndDate
    ) {
      return false;
    }

    return (
      repeatPolicy[RepeatType[repeatTerm as keyof typeof RepeatType]] ===
      repeatFrequency
    );
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
    rawFollowings.forEach((follower) => {
      let isJoined;
      const eventMember = event.eventMembers.find(
        (eventMember) => eventMember.user.id === follower.follower.id,
      );
      if (!eventMember) {
        invites.find((invite) => {
          if (invite.receiver.id === follower.follower.id) {
            isJoined = this.inviteService.transformStatusEnumResponse(
              invite.status.displayName,
            );
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
        // isJoined: eventMember ? true : false,
        isJoined: isJoined,
      });
    });
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
    rawFollowers.forEach((follower) => {
      let isJoined;
      const eventMember = event.eventMembers.find(
        (eventMember) => eventMember.user.id === follower.user.id,
      );
      if (!eventMember) {
        invites.find((invite) => {
          if (invite.receiver.id === follower.user.id) {
            isJoined = this.inviteService.transformStatusEnumResponse(
              invite.status.displayName,
            );
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
    });
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
    let isJoined;
    if (!eventMember) {
      invites.find((invite) => {
        if (invite.receiver.id === findByNickname.id) {
          isJoined = this.inviteService.transformStatusEnumResponse(
            invite.status.displayName,
          );
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

    if (!invitedUser || invitedUser.fcmToken === null) {
      throw new UserNotFoundException();
    }

    await this.inviteService.sendInviteEventMessage(
      user,
      event,
      invitedUser,
      invitedUser.fcmToken,
    );
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
