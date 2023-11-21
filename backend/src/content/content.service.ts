import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Content } from './entities/content.entity';

@Injectable()
export class ContentService {
  constructor(
    @InjectRepository(Content) private contentRepository: Repository<Content>,
  ) {}

  async createContent(file: Express.Multer.File) {
    const content = this.contentRepository.create({
      mimeType: file.mimetype,
      path: file.filename,
      type: file.mimetype.split('/').at(0),
    });

    return await this.contentRepository.save(content);
  }

  async updateContent(id: number, file: Express.Multer.File) {
    const updatedContent = this.contentRepository.create({
      mimeType: file.mimetype,
      path: file.filename,
      type: file.mimetype.split('/').at(0),
    });

    await this.contentRepository.update(id, updatedContent);
    return await this.contentRepository.findOne({ where: { id } });
  }
}
