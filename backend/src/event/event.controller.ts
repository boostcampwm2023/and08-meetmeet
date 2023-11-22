import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  Param,
  ParseIntPipe,
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
  ) {
    return await this.eventService.deleteEvent(user, eventId);
  }
}
