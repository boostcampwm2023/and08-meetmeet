import {
  Body,
  Controller,
  DefaultValuePipe,
  Delete,
  Get,
  Patch,
  Post,
  Query,
  UploadedFile,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import {
  ApiBearerAuth,
  ApiBody,
  ApiConsumes,
  ApiOkResponse,
  ApiOperation,
  ApiQuery,
  ApiTags,
} from '@nestjs/swagger';
import { GetUser } from 'src/auth/get-user.decorator';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { User } from './entities/user.entity';
import { UserService } from './user.service';
import { SearchResponseDto } from '../event/dto/search-response.dto';

@ApiBearerAuth()
@ApiTags('user')
@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @UseGuards(JwtAuthGuard)
  @Get('info')
  @ApiOperation({
    summary: '사용자 프로필 조회 API',
    description: 'kakao 회원의 경우 email에 kakao라고 조회됩니다.',
  })
  getUserInfo(@GetUser() user: User) {
    return this.userService.getUserInfo(user);
  }

  @UseGuards(JwtAuthGuard)
  @Patch('nickname')
  @ApiOperation({
    summary: '사용자 닉네임 수정 API',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        nickname: {
          type: 'string',
          description: '변경할 nickname',
        },
      },
    },
  })
  updateUserNickname(
    @GetUser() user: User,
    @Body('nickname') nickname: string,
  ) {
    return this.userService.updateUserNickname(user, nickname);
  }

  @UseGuards(JwtAuthGuard)
  @UseInterceptors(FileInterceptor('profile'))
  @Patch('profile')
  @ApiOperation({
    summary: '사용자 프로필 사진 수정 API',
  })
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        profile: {
          type: 'string',
          format: 'binary',
        },
      },
    },
  })
  updateUserProfile(
    @UploadedFile() profile: Express.Multer.File,
    @GetUser() user: User,
  ) {
    return this.userService.updateUserProfile(user, profile);
  }

  @UseGuards(JwtAuthGuard)
  @Patch('account')
  @ApiOperation({
    summary: '사용자 계정(password) 수정 API',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        password: {
          type: 'string',
          description: '변경할 password',
        },
      },
    },
  })
  updateUserPassword(
    @GetUser() user: User,
    @Body('password') password: string,
  ) {
    return this.userService.updateUserPassword(user, password);
  }

  @UseGuards(JwtAuthGuard)
  @Delete()
  @ApiOperation({
    summary: '사용자 탈퇴 API',
    description: 'parameter의 id와 access token의 user id가 같아야 합니다.',
  })
  deleteUser(@GetUser() user: User) {
    return this.userService.deleteUser(user);
  }

  @UseGuards(JwtAuthGuard)
  @Get('search')
  @ApiOperation({
    summary: '사용자 검색 API',
    description: 'parameter의 nickname으로 검색합니다.',
  })
  @ApiOkResponse({ type: SearchResponseDto })
  searchUser(@GetUser() user: User, @Query('nickname') nickname: string) {
    return this.userService.searchUser(user, nickname);
  }

  @UseGuards(JwtAuthGuard)
  @Post('fcm')
  @ApiOperation({
    summary: '사용자 FCM 토큰 등록 API',
    description: 'parameter의 token으로 FCM 토큰을 등록합니다.',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        token: {
          type: 'string',
          description: 'FCM 토큰',
        },
      },
    },
  })
  registerFCMToken(@GetUser() user: User, @Body('token') token: string) {
    return this.userService.registerFCMToken(user, token);
  }

  @UseGuards(JwtAuthGuard)
  @Post('logout')
  @ApiOperation({
    summary: '사용자 로그아웃 API',
  })
  logout(@GetUser() user: User) {
    return this.userService.logout(user);
  }

  @UseGuards(JwtAuthGuard)
  @Get('notification')
  @ApiOperation({
    summary: '사용자 알림 조회 API',
  })
  @ApiQuery({
    name: 'page',
    required: false,
    example: 'follow or invite',
    description: 'follow: 팔로우 알림(디폴트), invite: 초대 알림',
  })
  getUserNotification(
    @GetUser() user: User,
    @Query('page', new DefaultValuePipe('follow')) page: string,
  ) {
    if (page !== 'follow' && page !== 'invite') {
      page = 'follow';
    }
    return this.userService.getUserNotification(user, page);
  }
}
