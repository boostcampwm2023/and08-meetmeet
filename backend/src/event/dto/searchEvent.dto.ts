import {
  IsNotEmpty,
  IsOptional,
  IsDateString,
  IsString,
  MaxLength,
} from 'class-validator';

export class SearchEventDto {
  @IsOptional()
  @IsString()
  @MaxLength(64)
  keyword: string;

  @IsNotEmpty()
  @IsDateString()
  startDate: string;

  @IsNotEmpty()
  @IsDateString()
  endDate: string;
}
