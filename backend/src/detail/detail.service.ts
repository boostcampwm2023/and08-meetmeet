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

  async createDetailBulk(createScheduleDto: CreateScheduleDto, count: number) {
    const detailArray = [];
    for (let i = 0; i < count; i++) {
      const detail = this.detailRepository.create({
        ...createScheduleDto,
      });
      detailArray.push(detail);
    }
    return await this.detailRepository.save(detailArray);
  }

  async updateDetail(detail: Detail, createScheduleDto: CreateScheduleDto) {
    return await this.detailRepository.save({
      ...detail,
      ...createScheduleDto,
    });
  }

  async deleteDetail(detail: Detail) {
    return await this.detailRepository.softDelete(detail.id);
  }
}
