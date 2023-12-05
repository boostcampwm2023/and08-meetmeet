import {
  BadRequestException,
  HttpException,
  HttpStatus,
  Injectable,
  NotFoundException,
  UnauthorizedException,
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
import { FollowService } from '../follow/follow.service';
import { EventsResponseDto } from './dto/events-response.dto';
import { EventResponseDto } from './dto/event-response.dto';
import { EventStoryResponseDto } from './dto/event-story-response.dto';
import { Detail } from '../detail/entities/detail.entity';
import { UserService } from '../user/user.service';
import { InviteService } from '../invite/invite.service';
import { StatusEnum } from '../invite/entities/status.enum';
import { SearchResponseDto } from './dto/search-response.dto';

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
        throw new Error('event detail is not valid');
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
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    const authority = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    )?.authority?.displayName;

    if (!authority || !['OWNER', 'MEMBER', 'ADMIN'].includes(authority)) {
      throw new NotFoundException('이벤트에 참여하지 않았습니다.');
    }

    const detail = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    );

    if (!detail) {
      throw new Error('Cannot find event detail');
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
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    return EventStoryResponseDto.of(event, user.id);
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
      const savedEventMembers = await this.eventMemberService.createEventMember(
        savedEvent,
        user,
        detail,
        authority,
      );
      return {
        events: [
          {
            id: savedEventMembers.event.id,
            startDate: savedEventMembers.event.startDate,
            endDate: savedEventMembers.event.endDate,
            title: savedEventMembers.event.title,
            repeatPolicyId: savedEventMembers.event.repeatPolicyId,
            isJoinable: savedEventMembers.event.isJoinable ? true : false,
            announcement: savedEventMembers.event.announcement,
            isVisible: savedEventMembers.detail.isVisible ? true : false,
            memo: savedEventMembers.detail.memo,
            color: savedEventMembers.detail.color,
            alarmMinutes: savedEventMembers.detail.alarmMinutes,
            authority: savedEventMembers.authority.displayName,
          },
        ],
      };
    }
  }

  async deleteEvent(user: User, eventId: number, isAll: boolean) {
    // todo : 일정 삭제 멤버일때 잘 조회되는지 확인해야한다.
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers', 'feeds'],
      where: {
        id: eventId,
        eventMembers: { user: { id: user.id } },
      },
    });

    if (!event) {
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }
    const isValidUser = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    );
    if (!isValidUser) {
      throw new HttpException(
        '이벤트에 참여하지 않았습니다.',
        HttpStatus.NOT_FOUND,
      );
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
      throw new HttpException(
        '반복되는 컨텐츠가 아닙니다.',
        HttpStatus.NOT_FOUND,
      );
    }

    if (!isAll) {
      if (!event.repeatPolicy) {
        await this.eventRepository.softRemove(event);
      } else {
        const eventsWithRepeatPolicy = await this.eventRepository.findBy({
          repeatPolicyId: event.repeatPolicyId,
        });
        if (eventsWithRepeatPolicy.length === 1) {
          await this.repeatPolicyRepository.softRemove(event.repeatPolicy);
        }
        await this.eventRepository.softRemove(event);
      }
    } else {
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
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers', 'repeatPolicy'],
      where: {
        id: eventId,
        eventMembers: { user: { id: user.id } },
      },
    });

    if (!event) {
      throw new HttpException('컨텐츠가 없습니다.', HttpStatus.NOT_FOUND);
    }

    const eventMember = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    );

    if (!eventMember) {
      throw new Error('detail is not valid');
    }

    if (eventMember.authority.displayName === 'MEMBER') {
      await this.detailService.updateDetail(
        eventMember.detail,
        updateScheduleDto,
      );
    } else if (eventMember.authority.displayName === 'OWNER') {
      if (!isAll) {
        if (!event.repeatPolicy) {
          // 이전이 반복일정이 아닌경우
          // 하나만 변경할때
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
          // 이전이 반복일정인 경우
          const eventsWithRepeatPolicy = await this.eventRepository.findBy({
            repeatPolicyId: event.repeatPolicyId,
          });

          if (eventsWithRepeatPolicy.length === 1) {
            await this.repeatPolicyRepository.softRemove(event.repeatPolicy);
          }

          if (!this.isReapeatPolicy(updateScheduleDto)) {
            // 반복일정이 아닌경우
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
            // 반복일정인 경우
            if (
              this.isEqualRepeatPolicy(event.repeatPolicy, updateScheduleDto)
            ) {
              // 반복일정이 이전과 같은경우
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
              // 이전과 반복일정이 다른경우
              await this.eventRepository.softRemove(event);
              // await this.eventMemberService.deleteEventMemberByEventId(event);

              const repeatPolicy =
                await this.createRepeatPolicy(updateScheduleDto);

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
      } else {
        // 주인이고 반복일정인 경우
        if (this.isEqualRepeatPolicy(event.repeatPolicy, updateScheduleDto)) {
          // 반복일정이 이전과 같은경우
          // Todo : 전체 detail을 수정해야한다.
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
          // 반복일정이 같지 않은 경우 -> 반복일정을 삭제하고 새로 생성(피드가 없는경우)
          // todo : test 후 리팩토링
          // const eventsWithRepeatPolicyAndNoFeed = await this.eventRepository
          //   .createQueryBuilder('event')
          //   .leftJoinAndSelect('event.eventMembers', 'eventMember')
          //   .leftJoinAndSelect('event.repeatPolicy', 'repeatPolicy')
          //   .leftJoinAndSelect('eventMember.detail', 'detail')
          //   .leftJoinAndSelect('eventMember.user', 'user')
          //   .leftJoin('Feed', 'feed', 'feed.event = event.id')
          //   .where('event.repeatPolicy IS NOT NULL')
          //   .andWhere('feed.event IS NULL')
          //   .andWhere('repeatPolicy.id = :repeatPolicyId', {
          //     repeatPolicyId: event.repeatPolicyId,
          //   })
          //   .getMany();

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
          // const deleteEvents = [];
          // const deleteDetails = [];
          // const deleteEventMembers = [];
          // for (const event1 of eventsWithRepeatPolicyAndNoFeed) {
          //   //     // todo 일괄 처리되도록 수정해야한다.
          //       deleteEvents.push(event1);
          //       deleteDetails.push(event1.eventMembers[0].detail);
          //       deleteEventMembers.push(event1.eventMembers[0]);
          //   await this.eventRepository.softRemove(event1);
          //   await this.eventMemberService.deleteEventMemberByEventId(event1);
          //
          //   for (const eventMember of event1.eventMembers) {
          //     if (eventMember.user.id === user.id) {
          //       await this.detailService.deleteDetail(eventMember.detail);
          //     }
          //   }
          // }

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
      throw new UnauthorizedException('일정 공지 권한이 없습니다.');
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
      const event_detail = event.eventMembers.find(
        (eventMember) => eventMember.user.id === user.id,
      );

      if (!event_detail) {
        throw new Error('event detail is not valid');
      }

      return EventsResponseDto.of(event, event_detail);
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

    return `${year}-${month}-${day}`;
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

  async getFollowingsEvents(user: User, eventId: number) {
    const rawFollowings = await this.followService.getRawFollowings(user);
    const event = await this.eventRepository.findOne({
      relations: ['eventMembers'],
      where: {
        id: eventId,
      },
    });
    if (!event) {
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }
    const result: any[] = [];
    rawFollowings.forEach((follower) => {
      const eventMember = event.eventMembers.find(
        (eventMember) => eventMember.user.id === follower.follower.id,
      );
      result.push({
        id: follower.follower.id,
        nickname: follower.follower.nickname,
        profile: follower.follower.profile?.path ?? null,
        isJoined: eventMember ? true : false,
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
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    const result: any[] = [];
    rawFollowers.forEach((follower) => {
      const eventMember = event.eventMembers.find(
        (eventMember) => eventMember.user.id === follower.user.id,
      );
      result.push({
        id: follower.user.id,
        nickname: follower.user.nickname,
        profile: follower.user.profile?.path ?? null,
        isJoined: eventMember ? true : false,
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
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    const findByNickname = await this.userService.findUserByNickname(nickname);
    if (!findByNickname) {
      return SearchResponseDto.of([], [], []);
    } else {
      if (findByNickname.id === user.id) {
        throw new HttpException(
          '자기 자신은 검색할 수 없습니다.',
          HttpStatus.BAD_REQUEST,
        );
      }

      const eventMember = event.eventMembers.find(
        (eventMember) => eventMember.user.id === findByNickname.id,
      );
      console.log(eventMember);
      return SearchResponseDto.of(
        [findByNickname],
        [],
        eventMember ? [true] : [false],
      );
    }
  }

  async inviteSchedule(user: User, userId: number, eventId: number) {
    const event = await this.eventRepository.findOne({
      where: { id: eventId },
    });

    if (!event) {
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    if (user.id === userId) {
      throw new HttpException(
        '자기 자신을 초대할 수 없습니다.',
        HttpStatus.BAD_REQUEST,
      );
    }

    event.eventMembers.forEach((eventMember) => {
      if (eventMember.user.id === userId) {
        throw new HttpException(
          '이미 참여한 유저입니다.',
          HttpStatus.BAD_REQUEST,
        );
      }
    });

    const invitedUser = await this.userService.findUserById(userId);

    if (!invitedUser || invitedUser.fcmToken === null) {
      throw new HttpException(
        '존재하지 않는 유저입니다.',
        HttpStatus.NOT_FOUND,
      );
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
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    event.eventMembers.forEach((eventMember) => {
      if (eventMember.user.id === user.id) {
        throw new HttpException(
          '이미 참여한 유저입니다.',
          HttpStatus.BAD_REQUEST,
        );
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
      throw new Error('authority is not valid');
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
      throw new HttpException('이벤트가 없습니다.', HttpStatus.NOT_FOUND);
    }

    const invite = await this.inviteService.getInviteById(inviteId);

    if (!invite) {
      throw new HttpException('초대가 없습니다.', HttpStatus.NOT_FOUND);
    }

    if (invite.status.displayName !== StatusEnum.Pending) {
      throw new HttpException(
        '이미 만료된 초대입니다.',
        HttpStatus.BAD_REQUEST,
      );
    }

    if (invite.receiver.id !== user.id) {
      throw new HttpException(
        '초대받은 유저가 아닙니다.',
        HttpStatus.BAD_REQUEST,
      );
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
        throw new Error('authority is not valid');
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
