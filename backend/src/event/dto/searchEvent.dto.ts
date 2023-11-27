import {
  IsNotEmpty,
  IsOptional,
  IsDateString,
  IsString,
  MaxLength,
} from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class SearchEventDto {
  @ApiProperty({ name: 'keyword', example: 'keyword' })
  @IsOptional()
  @IsString()
  @MaxLength(64)
  keyword: string;

  @ApiProperty({ name: 'startDate', example: '2021-01-01' })
  @IsNotEmpty()
  @IsDateString()
  startDate: string;

  @ApiProperty({ name: 'endDate', example: '2021-01-01' })
  @IsNotEmpty()
  @IsDateString()
  endDate: string;
}
