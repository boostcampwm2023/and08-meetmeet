import {
  IsBoolean,
  IsDateString,
  IsEnum,
  IsOptional,
  IsString,
  Length,
} from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export enum RepeatTerm {
  DAY = 'DAY',
  WEEK = 'WEEK',
  MONTH = 'MONTH',
  YEAR = 'YEAR',
}
export class CreateScheduleDto {
  @ApiProperty({ name: 'title', example: 'title' })
  @IsString()
  @Length(1, 64)
  title: string;

  @ApiProperty({ name: 'startDate', example: '2021-01-01' })
  @IsDateString()
  startDate: Date;

  @ApiProperty({ name: 'endDate', example: '2021-01-01' })
  @IsDateString()
  endDate: Date;

  @ApiProperty({ name: 'isJoinable', example: true })
  @IsBoolean()
  isJoinable: boolean;

  @ApiProperty({ name: 'isVisible', example: true })
  @IsBoolean()
  isVisible: boolean;

  @ApiProperty({ name: 'memo', example: 'memo' })
  @IsOptional()
  @IsString()
  @Length(1, 64)
  memo?: string;

  @ApiProperty({ name: 'RepeatTerm', example: 'DAY' })
  @IsOptional()
  @IsEnum(RepeatTerm)
  repeatTerm?: RepeatTerm;

  @ApiProperty({ name: 'repeatFrequency', example: 1 })
  @IsOptional()
  repeatFrequency?: number;

  @ApiProperty({ name: 'repeatEndDate', example: '2021-01-01' })
  @IsOptional()
  @IsDateString()
  repeatEndDate?: Date;
}
