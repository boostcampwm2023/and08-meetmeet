import { ApiBearerAuth, ApiOperation, ApiTags } from '@nestjs/swagger';
import { Body, Controller, Get, Post, UseGuards } from '@nestjs/common';
import { FollowService } from './follow.service';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { User } from '../user/entities/user.entity';
import { GetUser } from '../auth/get-user.decorator';

@ApiBearerAuth()
@ApiTags('follow')
@Controller('follow')
export class FollowController {
  constructor(private readonly followService: FollowService) {}

  @UseGuards(JwtAuthGuard)
  @Get('followers')
  @ApiOperation({
    summary: '팔로워 목록 조회 API',
  })
  async getFollowers(@GetUser() user: User) {
    return await this.followService.getFollowers(user);
  }

  @UseGuards(JwtAuthGuard)
  @Get('followings')
  @ApiOperation({
    summary: '팔로잉 목록 조회 API',
  })
  async getFollowings(@GetUser() user: User) {
    return await this.followService.getFollowings(user);
  }

  @UseGuards(JwtAuthGuard)
  @Post('')
  @ApiOperation({
    summary: '팔로우 API',
  })
  async follow(@GetUser() user: User, @Body() userId: number) {
    return await this.followService.follow(user, userId);
  }
}
