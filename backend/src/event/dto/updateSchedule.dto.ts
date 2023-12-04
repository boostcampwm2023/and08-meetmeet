import {
  IsBoolean,
  IsDateString,
  IsEnum,
  IsInt,
  IsOptional,
  IsString,
  Length,
} from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';
import { RepeatTerm } from './createSchedule.dto';

export class UpdateScheduleDto {
  @ApiProperty({ name: 'title', example: 'title' })
  @IsOptional()
  @IsString()
  @Length(1, 64)
  title: string;

  @ApiProperty({ name: 'startDate', example: '2023-11-01T03:00:00.000Z' })
  @IsOptional()
  @IsDateString()
  startDate: Date;

  @ApiProperty({ name: 'endDate', example: '2023-11-30T03:00:00.000Z' })
  @IsOptional()
  @IsDateString()
  endDate: Date;

  @ApiProperty({ name: 'isJoinable', example: true })
  @IsOptional()
  @IsBoolean()
  isJoinable: boolean;

  @ApiProperty({ name: 'isVisible', example: true })
  @IsOptional()
  @IsBoolean()
  isVisible: boolean;

  @ApiProperty({ name: 'memo', example: 'memo' })
  @IsOptional()
  @IsString()
  @Length(1, 64)
  memo?: string;

  @ApiProperty({ name: 'color', example: 10 })
  @IsOptional()
  @IsInt()
  color: number;

  @ApiProperty({ name: 'alarmMinutes', example: 10 })
  @IsOptional()
  @IsInt()
  alarmMinutes: number;

  @ApiProperty({ name: 'repeatTerm', example: 'DAY' })
  @IsOptional()
  @IsEnum(RepeatTerm)
  repeatTerm?: RepeatTerm;

  @ApiProperty({ name: 'repeatFrequency', example: 1 })
  @IsOptional()
  repeatFrequency?: number;

  @ApiProperty({ name: 'repeatEndDate', example: '2023-11-01T03:00:00.000Z' })
  @IsOptional()
  @IsDateString()
  repeatEndDate?: Date;
}
