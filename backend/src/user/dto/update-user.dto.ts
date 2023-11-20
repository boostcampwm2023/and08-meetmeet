import { ApiProperty } from '@nestjs/swagger';
import { IsOptional, IsString } from 'class-validator';

export class UpdateUserDto {
  @ApiProperty({ name: 'nickname', example: 'nickname' })
  @IsOptional()
  @IsString()
  nickname: string;

  // TODO: profile image update

  @ApiProperty({ name: 'email', example: 'email' })
  @IsOptional()
  @IsString()
  email: string;

  @ApiProperty({ name: 'password', example: 'password' })
  @IsOptional()
  @IsString()
  password: string;
}
