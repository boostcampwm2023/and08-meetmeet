import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsNotEmpty, IsString } from 'class-validator';

export class AuthUserDto {
  @ApiProperty({ name: 'email', example: 'user@example.com' })
  @IsNotEmpty()
  @IsString()
  @IsEmail()
  email: string;

  @ApiProperty({ name: 'password', example: 'password' })
  @IsNotEmpty()
  @IsString()
  password: string;
}
