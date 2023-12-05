import {
  Body,
  Controller,
  DefaultValuePipe,
  Delete,
  Get,
  HttpCode,
  Param,
  ParseBoolPipe,
  ParseIntPipe,
  Patch,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiBody,
  ApiOkResponse,
  ApiOperation,
  ApiParam,
  ApiQuery,
  ApiTags,
} from '@nestjs/swagger';
import { EventService } from './event.service';
import { GetUser } from '../auth/get-user.decorator';
import { User } from '../user/entities/user.entity';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CreateScheduleDto } from './dto/createSchedule.dto';
import { SearchEventDto } from './dto/searchEvent.dto';
import { UpdateScheduleDto } from './dto/updateSchedule.dto';
import { EventsResponseDto } from './dto/events-response.dto';
import { EventResponseDto } from './dto/event-response.dto';
import { EventStoryResponseDto } from './dto/event-story-response.dto';
import {SearchResponseDto} from "./dto/search-response.dto";

@ApiBearerAuth()
@ApiTags('event')
@Controller('event')
export class EventController {
  constructor(private readonly eventService: EventService) {}

  @UseGuards(JwtAuthGuard)
  @Get('/search')
  @ApiOperation({
    summary: '일정 검색 API',
    description: '',
  })
  async searchEvent(
    @GetUser() user: User,
    @Query() searchEventDto: SearchEventDto,
  ) {
    return await this.eventService.searchEvent(user, searchEventDto);
  }

  @UseGuards(JwtAuthGuard)
  @Get()
  @ApiOperation({
    summary: '일정 조회 API',
    description: '입력된 startDate, endDate로 조회합니다.',
  })
  @ApiQuery({
    name: 'startDate',
    required: true,
    example: '2023-11-01T03:00:00.000Z',
  })
  @ApiQuery({
    name: 'endDate',
    required: true,
    example: '2023-11-30T03:00:00.000Z',
  })
  @ApiOkResponse({ type: EventsResponseDto })
  async getEvents(
    @GetUser() user: User,
    @Query('startDate') startDate: string,
    @Query('endDate') endDate: string,
  ) {
    return await this.eventService.getEvents(user, startDate, endDate);
  }

  @UseGuards(JwtAuthGuard)
  @Get('/:eventId')
  @ApiOperation({
    summary: '일정 상세 조회 API',
    description: '',
  })
  @ApiParam({
    name: 'eventId',
    required: true,
    example: 123,
  })
  @ApiOkResponse({ type: EventResponseDto })
  async getEvent(
    @GetUser() user: User,
    @Param('eventId', new ParseIntPipe({ errorHttpStatusCode: 400 }))
    eventId: number,
  ) {
    return await this.eventService.getEvent(user, eventId);
  }

  @UseGuards(JwtAuthGuard)
  @Get('/:event_id/feeds')
  @ApiOperation({
    summary: '일정 피드 조회 API',
    description: '',
  })
  @ApiParam({
    name: 'event_id',
    required: true,
    example: 123,
  })
  @ApiOkResponse({ type: EventStoryResponseDto })
  async getEventFeeds(
    @GetUser() user: User,
    @Param('event_id', new ParseIntPipe({ errorHttpStatusCode: 400 }))
    eventId: number,
  ) {
    return await this.eventService.getEventFeeds(user, eventId);
  }

  @UseGuards(JwtAuthGuard)
  @HttpCode(201)
  @Post('')
  @ApiOperation({
    summary: '일정 생성 API',
    description: '',
  })
  @ApiBody({
    type: CreateScheduleDto,
  })
  async createEvent(
    @GetUser() user: User,
    @Body() createScheduleDto: CreateScheduleDto,
  ) {
    return await this.eventService.createEvent(user, createScheduleDto);
  }

  @UseGuards(JwtAuthGuard)
  @HttpCode(204)
  @Delete('/:eventId')
  @ApiOperation({
    summary: '일정 삭제 API',
    description: '',
  })
  @ApiParam({
    name: 'eventId',
    required: true,
    example: 123,
  })
  @ApiQuery({
    name: 'isAll',
    required: false,
    description: 'true를 제외한 모든건 false로 처리됩니다.',
  })
  async deleteEvent(
    @GetUser() user: User,
    @Param('eventId', new ParseIntPipe({ errorHttpStatusCode: 400 }))
    eventId: number,
    @Query('isAll', new DefaultValuePipe(false), ParseBoolPipe) isAll: boolean,
  ) {
    return await this.eventService.deleteEvent(user, eventId, isAll);
  }

  @UseGuards(JwtAuthGuard)
  @Patch('/:eventId/announcement')
  @ApiOperation({
    summary: '일정 공지 수정 API',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        announcement: {
          type: 'string',
        },
      },
    },
  })
  async updateEventAnnouncement(
    @GetUser() user: User,
    @Param('eventId', new ParseIntPipe({ errorHttpStatusCode: 400 }))
    eventId: number,
    @Body('announcement') announcement: string,
  ) {
    return await this.eventService.updateEventAnnouncement(
      user,
      eventId,
      announcement,
    );
  }

  @UseGuards(JwtAuthGuard)
  @Patch('/:eventId')
  @ApiOperation({
    summary: '일정 수정 API',
    description: '',
  })
  @ApiQuery({
    name: 'isAll',
    required: false,
    description: 'true를 제외한 모든건 false로 처리됩니다.',
  })
  async updateEvent(
    @GetUser() user: User,
    @Param('eventId', new ParseIntPipe({ errorHttpStatusCode: 400 }))
    eventId: number,
    @Query('isAll', new DefaultValuePipe(false), ParseBoolPipe) isAll: boolean,
    @Body() updateScheduleDto: UpdateScheduleDto,
  ) {
    return await this.eventService.updateEvent(
      user,
      eventId,
      updateScheduleDto,
      isAll,
    );
  }

  @UseGuards(JwtAuthGuard)
  @Get('/user/followings')
  @ApiOperation({
    summary: '일정 초대 관련 팔로잉 조회',
    description: '',
  })
  @ApiQuery({
    name: 'eventId',
    required: true,
    example: 123,
  })
  async getFollowingsEvents(
    @GetUser() user: User,
    @Query('eventId') eventId: number,
  ) {
    return await this.eventService.getFollowingsEvents(user, eventId);
  }

  @UseGuards(JwtAuthGuard)
  @Get('/user/followers')
  @ApiOperation({
    summary: '일정 초대 관련 팔로우 조회',
    description: '',
  })
  @ApiQuery({
    name: 'eventId',
    required: true,
    example: 123,
  })
  async getFollowersEvents(
    @GetUser() user: User,
    @Query('eventId', ParseIntPipe) eventId: number,
  ) {
    return await this.eventService.getFollowersEvents(user, eventId);
  }

  @UseGuards(JwtAuthGuard)
  @Get('/user/search/:eventId')
  @ApiOperation({
    summary: '일정 초대 관련 유저 검색',
    description: '',
  })
  @ApiQuery({
    name: 'nickname',
    required: true,
    example: 'nickname',
  })
  @ApiParam({
    name: 'eventId',
    required: true,
    example: 123,
  })
  @ApiOkResponse({ type: SearchResponseDto })
  async searchUserEvents(
    @GetUser() user: User,
    @Param('eventId', ParseIntPipe) eventId: number,
    @Query('nickname') nickname: string,
  ) {
    return await this.eventService.searchUserEvents(user, nickname, eventId);
  }

  @UseGuards(JwtAuthGuard)
  @Post('/schedule/invite')
  @ApiOperation({
    summary: '일정 초대 API',
    description: '',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        userId: {
          type: 'number',
        },
        eventId: {
          type: 'number',
        },
      },
    },
  })
  async inviteSchedule(
    @GetUser() user: User,
    @Body('userId', ParseIntPipe) userId: number,
    @Body('eventId', ParseIntPipe) eventId: number,
  ) {
    return await this.eventService.inviteSchedule(user, userId, eventId);
  }

  @UseGuards(JwtAuthGuard)
  @Post('/schedule/join')
  @ApiOperation({
    summary: '일정 참여 API',
    description: '',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        eventId: {
          type: 'number',
        },
      },
    },
  })
  async cancelSchedule(
    @GetUser() user: User,
    @Body('eventId', ParseIntPipe) eventId: number,
  ) {
    return await this.eventService.joinSchedule(user, eventId);
  }
  @UseGuards(JwtAuthGuard)
  @Post('/schedule/accept')
  @ApiOperation({
    summary: '일정 참여 수락 API',
    description: '',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        inviteId: {
          type: 'number',
        },
        eventId: {
          type: 'number',
        },
      },
    },
  })
  @ApiQuery({
    name: 'accept',
    required: false,
    description: 'true를 제외한 모든건 false로 처리됩니다.',
  })
  async acceptSchedule(
    @Body('eventId', ParseIntPipe) eventId: number,
    @Body('inviteId', ParseIntPipe) inviteId: number,
    @GetUser() user: User,
    @Query('accept', new DefaultValuePipe(false), ParseBoolPipe)
    accept: boolean,
  ) {
    return await this.eventService.acceptSchedule(
      user,
      eventId,
      inviteId,
      accept,
    );
  }

  @UseGuards(JwtAuthGuard)
  @Get('/user/:userId')
  @ApiOperation({
    summary: '일정 조회 API',
    description: '입력된 startDate, endDate로 조회합니다.',
  })
  @ApiQuery({
    name: 'startDate',
    required: true,
    example: '2023-11-01T03:00:00.000Z',
  })
  @ApiQuery({
    name: 'endDate',
    required: true,
    example: '2023-11-30T03:00:00.000Z',
  })
  @ApiOkResponse({ type: EventsResponseDto })
  async getUserEvents(
    @GetUser() user: User,
    @Param('userId', new ParseIntPipe({ errorHttpStatusCode: 400 }))
    userId: number,
    @Query('startDate') startDate: string,
    @Query('endDate') endDate: string,
  ) {
    return await this.eventService.getUserEvents(
      user,
      userId,
      startDate,
      endDate,
    );
  }
}
