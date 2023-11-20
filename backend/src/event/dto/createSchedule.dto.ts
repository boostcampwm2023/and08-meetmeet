import {
  IsBoolean,
  IsDateString,
  IsEnum,
  IsOptional,
  IsString,
  Length,
} from 'class-validator';

export enum RepeatTerm {
  DAY = 'DAY',
  WEEK = 'WEEK',
  MONTH = 'MONTH',
  YEAR = 'YEAR',
}
export class CreateScheduleDto {
  @IsString()
  @Length(1, 64)
  title: string;

  @IsDateString()
  startDate: string;

  @IsDateString()
  endDate: string;

  @IsBoolean()
  isJoinable: boolean;

  @IsBoolean()
  isVisible: boolean;

  @IsOptional()
  @IsString()
  @Length(1, 64)
  memo?: string;

  @IsOptional()
  @IsEnum(RepeatTerm)
  repeatTerm?: RepeatTerm;

  @IsOptional()
  repeatFrequency?: number;

  @IsOptional()
  @IsDateString()
  repeatEndDate?: Date;
}
