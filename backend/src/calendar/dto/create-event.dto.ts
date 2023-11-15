import { IsNotEmpty } from 'class-validator';

const RepeatTerm = {
  DAY: 'Day',
  WEEK: 'Week',
  MONTH: 'Month',
  YEAR: 'Year',
} as const;

type RepeatTerm = keyof typeof RepeatTerm;

export class CreateEventDto {
  @IsNotEmpty()
  title: string;

  @IsNotEmpty()
  startDate: string;

  @IsNotEmpty()
  endDate: string;

  @IsNotEmpty()
  isJoinable: boolean;

  @IsNotEmpty()
  isVisible: boolean;

  memo?: string;
  repeatTerm?: RepeatTerm;
  repeatFrequency?: number;
  repeatEndDate?: string;
}
