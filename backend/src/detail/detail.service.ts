import { Injectable } from '@nestjs/common';
import { Detail } from './entities/detail.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { CreateScheduleDto } from '../event/dto/createSchedule.dto';

@Injectable()
export class DetailService {
  constructor(
    @InjectRepository(Detail)
    private detailRepository: Repository<Detail>,
  ) {}

  async createDetail(createScheduleDto: CreateScheduleDto) {
    const detail = this.detailRepository.create({
      ...createScheduleDto,
    });
    return await this.detailRepository.save(detail);
  }
}
