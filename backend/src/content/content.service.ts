import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { In, Repository } from 'typeorm';
import { Content } from './entities/content.entity';

@Injectable()
export class ContentService {
  constructor(
    @InjectRepository(Content) private contentRepository: Repository<Content>,
  ) {}

  async createContent(file: Express.Multer.File) {
    const content = this.createEntity(file);

    return await this.contentRepository.save(content);
  }

  async createContentBulk(files: Array<Express.Multer.File>) {
    const contents = files.reduce((acc, file) => {
      const content = this.createEntity(file);
      return [...acc, content];
    }, []);

    const insertResult = await this.contentRepository.insert(contents);
    const contentsId = insertResult.identifiers.map((value) => value.id);

    return await this.contentRepository.find({ where: { id: In(contentsId) } });
  }

  private createEntity(file: Express.Multer.File) {
    return this.contentRepository.create({
      mimeType: file.mimetype,
      path: file.filename,
      type: file.mimetype.split('/').at(0),
    });
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

  async softDeleteContent(idList: number[]) {
    await this.contentRepository.softDelete(idList);
  }
}
