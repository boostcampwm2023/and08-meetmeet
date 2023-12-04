import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class UpdateUserInfoDto {
  @ApiProperty({
    name: 'nickname',
    example: 'nickname',
    required: false,
  })
  @IsOptional()
  @IsNotEmpty()
  @IsString()
  nickname: string;

  @ApiProperty({
    name: 'profile',
    type: 'string',
    format: 'binary',
    required: false,
  })
  @IsOptional()
  profile: Express.Multer.File;
}
