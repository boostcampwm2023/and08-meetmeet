import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import { IsInt, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateFeedDto {
  @ApiProperty({ name: 'eventId', example: 1 })
  @Type(() => Number)
  @IsNotEmpty()
  @IsInt()
  eventId: number;

  @ApiProperty({ name: 'memo', example: 'memo', required: false })
  @IsOptional()
  @IsNotEmpty()
  @IsString()
  memo: string;

  @ApiProperty({
    name: 'contents',
    type: 'array',
    items: { type: 'string', format: 'binary' },
    required: false,
  })
  @IsOptional()
  contents: Array<Express.Multer.File>;
}
