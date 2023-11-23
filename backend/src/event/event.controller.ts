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
import { ApiBearerAuth, ApiBody, ApiOperation, ApiTags } from '@nestjs/swagger';
import { EventService } from './event.service';
import { GetUser } from '../auth/get-user.decorator';
import { User } from '../user/entities/user.entity';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CreateScheduleDto } from './dto/createSchedule.dto';
import { SearchEventDto } from './dto/searchEvent.dto';
import { UpdateScheduleDto } from './dto/updateSchedule.dto';

@ApiBearerAuth()
@ApiTags('event')
@Controller('event')
export class EventController {
  constructor(private readonly eventService: EventService) {}

  @UseGuards(JwtAuthGuard)
  @Get()
  @ApiOperation({
    summary: '일정 조회 API',
    description: '입력된 startDate, endDate로 조회합니다.',
  })
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
  async deleteEvent(
    @GetUser() user: User,
    @Param('eventId', new ParseIntPipe({ errorHttpStatusCode: 400 }))
    eventId: number,
    @Query('isAll', new DefaultValuePipe(false), ParseBoolPipe) isAll: boolean,
  ) {
    return await this.eventService.deleteEvent(user, eventId, isAll);
  }

  @UseGuards(JwtAuthGuard)
  @Patch('/:eventId')
  @ApiOperation({
    summary: '일정 수정 API',
    description: '',
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
}
