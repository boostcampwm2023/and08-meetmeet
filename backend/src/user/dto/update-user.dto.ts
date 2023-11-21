import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class UpdateUserDto {
  @ApiProperty({
    name: 'nickname',
    example: 'nickname',
    required: false,
  })
  @IsOptional()
  @IsNotEmpty()
  @IsString()
  nickname: string;

  // TODO: profile image update
  @ApiProperty({
    name: 'profileImage',
    type: 'string',
    format: 'binary',
    required: false,
  })
  @IsOptional()
  profile: Express.Multer.File;

  @ApiProperty({ name: 'password', example: 'password', required: false })
  @IsOptional()
  @IsNotEmpty()
  @IsString()
  password: string;
}
