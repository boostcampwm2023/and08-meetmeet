import { Injectable } from '@nestjs/common';
import { Detail } from './entities/detail.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { In, Repository } from 'typeorm';
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

  // todo : detail insert interface 생성
  async createDetailSingle(detail: Detail) {
    return await this.detailRepository.save(detail);
  }

  async createDetailBulk(createScheduleDto: CreateScheduleDto, count: number) {
    const detailArray = [...new Array(count)].map(() =>
      this.detailRepository.create({
        ...createScheduleDto,
      }),
    );

    const result = await this.detailRepository.insert(detailArray);
    return await this.detailRepository.find({
      where: { id: In(result.identifiers.map((identifier) => identifier.id)) },
    });
  }

  async updateDetail(detail: Detail, createScheduleDto: CreateScheduleDto) {
    await this.detailRepository.save({ ...detail, ...createScheduleDto });
  }

  async deleteDetailsById(idList: number[]) {
    if (!idList.length) {
      return;
    }
    await this.detailRepository.softDelete(idList);
  }

  async updateDetailBulk(
    detailIds: number[],
    createScheduleDto: CreateScheduleDto,
  ) {
    const detail = this.detailRepository.create({ ...createScheduleDto });

    await this.detailRepository.update(detailIds, detail);
  }
}
