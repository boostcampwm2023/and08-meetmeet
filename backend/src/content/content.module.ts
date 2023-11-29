import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ContentController } from './content.controller';
import { ContentService } from './content.service';
import { Content } from './entities/content.entity';
import { ObjectStorage } from './object-storage';

@Module({
  imports: [TypeOrmModule.forFeature([Content])],
  controllers: [ContentController],
  providers: [
    ContentService,
    { provide: ObjectStorage, useClass: ObjectStorage },
  ],
  exports: [ContentService],
})
export class ContentModule {}
