import { Body, Controller, Post, UseGuards } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { User } from 'src/user/entities/user.entity';
import { AuthService } from './auth.service';
import { AuthUserDto } from './dto/auth-user.dto';
import { GetUser } from './get-user.decorator';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('register')
  register(@Body() authUserDto: AuthUserDto) {
    return this.authService.register(authUserDto);
  }

  @UseGuards(AuthGuard('local'))
  @Post('login')
  login(@GetUser() user: User) {
    return this.authService.login(user);
  }
}
