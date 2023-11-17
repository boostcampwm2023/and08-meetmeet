import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Comment } from './entities/comment.entity';

@Module({ imports: [TypeOrmModule.forFeature([Comment])] })
export class CommentModule {}
