import { Body, Controller, Get, Post, UseGuards } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { ApiBearerAuth, ApiBody, ApiTags } from '@nestjs/swagger';
import { User } from 'src/user/entities/user.entity';
import { AuthService } from './auth.service';
import { AuthUserDto } from './dto/auth-user.dto';
import { GetUser } from './get-user.decorator';
import { JwtAuthGuard } from './jwt-auth.guard';

@ApiTags('auth')
@ApiBearerAuth()
@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('register')
  register(@Body() authUserDto: AuthUserDto) {
    return this.authService.register(authUserDto);
  }

  @ApiBody({
    description: 'post swagger',
    type: AuthUserDto,
  })
  @UseGuards(AuthGuard('local'))
  @Post('login')
  login(@GetUser() user: User) {
    return this.authService.login(user);
  }

  @UseGuards(JwtAuthGuard)
  @Get('refresh')
  refresh(@GetUser() user: User) {
    return this.authService.refresh(user);
  }

  @UseGuards(JwtAuthGuard)
  @Post('test')
  test(@GetUser() user: User, @Body() start: Date) {
    console.log(user, start);
    return user;
  }
}
