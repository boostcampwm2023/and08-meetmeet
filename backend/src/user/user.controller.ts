import {
  Body,
  Controller,
  Delete,
  Param,
  Patch,
  UseGuards,
} from '@nestjs/common';
import { ApiBearerAuth, ApiOperation, ApiTags } from '@nestjs/swagger';
import { GetUser } from 'src/auth/get-user.decorator';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { UpdateUserDto } from './dto/update-user.dto';
import { User } from './entities/user.entity';
import { UserService } from './user.service';

@ApiBearerAuth()
@ApiTags('user')
@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @UseGuards(JwtAuthGuard)
  @Patch(':id/info')
  @ApiOperation({
    summary: '사용자 프로필, 계정 수정 API',
    description: 'parameter의 id와 access token의 user id가 같아야 합니다.',
  })
  updateUserInfo(
    @Param('id') id: number,
    @GetUser() user: User,
    @Body() updateUserDto: UpdateUserDto,
  ) {
    return this.userService.updateUser(id, user, updateUserDto);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id')
  @ApiOperation({
    summary: '사용자 탈퇴 API',
    description: 'parameter의 id와 access token의 user id가 같아야 합니다.',
  })
  deleteUser(@Param('id') id: number, @GetUser() user: User) {
    return this.userService.deleteUser(id, user);
  }
}
