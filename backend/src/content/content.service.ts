import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { v4 as uuidv4 } from 'uuid';
import { In, Repository } from 'typeorm';
import { Content } from './entities/content.entity';
import { ObjectStorage } from './object-storage';
import { extname } from 'path';
import { ConfigService } from '@nestjs/config';
import {
  ContentNotFoundException,
  ObjectStorageUploadException,
} from './exception/content.exception';

@Injectable()
export class ContentService {
  locationPrefix: string;
  constructor(
    @InjectRepository(Content) private contentRepository: Repository<Content>,
    private readonly objectStorage: ObjectStorage,
    private readonly configService: ConfigService,
  ) {
    this.locationPrefix = configService.get('CONTENT_PREFIX', '');
  }

  async createContent(file: Express.Multer.File, dir: string) {
    file.path =
      this.locationPrefix + this.generateFilePath(dir, file.originalname);
    const content = this.createEntity(file);

    await this.objectStorage.upload(file).catch(() => {
      throw new ObjectStorageUploadException();
    });
    return await this.contentRepository.save(content);
  }

  async createContentBulk(files: Array<Express.Multer.File>, dir: string) {
    const contents = files.reduce((acc, file) => {
      file.path =
        this.locationPrefix + this.generateFilePath(dir, file.originalname);
      const content = this.createEntity(file);
      return [...acc, content];
    }, []);

    await Promise.all(
      files.map((file) => this.objectStorage.upload(file)),
    ).catch(() => {
      throw new ObjectStorageUploadException();
    });

    const insertResult = await this.contentRepository.insert(contents);
    const contentsId = insertResult.identifiers.map((value) => value.id);

    return await this.contentRepository.find({ where: { id: In(contentsId) } });
  }

  private createEntity(file: Express.Multer.File) {
    return this.contentRepository.create({
      mimeType: file.mimetype,
      path: file.path,
      type: file.mimetype.split('/').at(0),
    });
  }

  async softDeleteContent(idList: number[]) {
    if (!idList.length) {
      return;
    }
    const files = await this.contentRepository.find({
      where: { id: In(idList) },
    });

    await this.contentRepository.softDelete(idList);
    // TODO: delete 시 object storage에서 바로 삭제할 것인지
    await this.objectStorage.deleteBulk(
      files.map((file) => {
        if (!file) {
          throw new ContentNotFoundException();
        }
        return file.path.replace(this.locationPrefix, '');
      }),
    );
  }

  generateFilePath(dir: string, fileName: string) {
    return `${dir}/${uuidv4().split('-').slice(2).join()}${extname(fileName)}`;
  }
}
