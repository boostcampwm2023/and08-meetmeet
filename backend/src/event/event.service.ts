import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Between, LessThan, MoreThan, Repository } from 'typeorm';
import { Event } from './entities/event.entity';

@Injectable()
export class EventService {
  constructor(
    @InjectRepository(Event)
    private eventRepository: Repository<Event>,
  ) {}

  async getEvents(startDate: string, endDate: string) {
    return await this.eventRepository.find({
      where: [
        {
          startDate: Between(new Date(startDate), new Date(endDate)),
        },
        { endDate: Between(new Date(startDate), new Date(endDate)) },
        {
          startDate: LessThan(new Date(startDate)),
          endDate: MoreThan(new Date(endDate)),
        },
      ],
    });
  }
}
