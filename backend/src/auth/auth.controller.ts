import {Body, Controller, Get, Post, Query, UseGuards, UseInterceptors,} from '@nestjs/common';
import {AuthGuard} from '@nestjs/passport';
import {ApiBody, ApiOperation, ApiTags} from '@nestjs/swagger';
import {User} from 'src/user/entities/user.entity';
import {AuthService} from './auth.service';
import {AuthUserDto} from './dto/auth-user.dto';
import {GetUser} from './get-user.decorator';
import {SlackInterceptor} from '../log/slack.interceptor';

@ApiTags('auth')
@Controller('auth')
@UseInterceptors(SlackInterceptor)
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('register')
  @ApiOperation({
    summary: 'email, password 회원가입 API',
  })
  register(@Body() authUserDto: AuthUserDto) {
    return this.authService.register(authUserDto);
  }

  @Post('kakao')
  @ApiOperation({
    summary: '카카오 로그인 API',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        kakaoId: {
          type: 'string',
          description: '카카오 API를 통해 받은 유저 정보',
        },
      },
    },
  })
  kakaoLogin(@Body('kakaoId') kakaoId: string) {
    return this.authService.kakaoLogin(kakaoId);
  }

  @UseGuards(AuthGuard('local'))
  @Post('login')
  @ApiOperation({
    summary: 'email, password 로그인 API',
  })
  @ApiBody({
    type: AuthUserDto,
  })
  localLogin(@GetUser() user: User) {
    return this.authService.login(user);
  }

  @Post('refresh')
  @ApiOperation({
    summary: 'access token 갱신 API',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        refreshToken: {
          type: 'string',
          description: '새로운 access token 발급을 위한 refresh token',
        },
      },
    },
  })
  async refresh(@Body('refreshToken') refreshToken: string) {
    return this.authService.refresh(refreshToken);
  }

  @Post('check/token')
  @ApiOperation({
    summary: 'access token 검증 API',
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        accessToken: {
          type: 'string',
          description: 'access token이 유효한지 검증한다.',
        },
      },
    },
  })
  checkAccessToken(@Body('token') token: string) {
    return this.authService.verifyToken(token);
  }

  @Get('check/email')
  @ApiOperation({
    summary: 'email 중복 확인 API',
  })
  checkEmail(@Query('email') email: string) {
    return this.authService.checkEmail(email);
  }

  @Get('check/nickname')
  @ApiOperation({
    summary: 'nickname 중복 확인 API',
  })
  checkNickname(@Query('nickname') nickname: string) {
    return this.authService.checkNickname(nickname);
  }
}
