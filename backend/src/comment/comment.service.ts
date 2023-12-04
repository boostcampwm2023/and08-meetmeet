import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Feed } from 'src/feed/entities/feed.entity';
import { User } from 'src/user/entities/user.entity';
import { Repository } from 'typeorm';
import { Comment } from './entities/comment.entity';

@Injectable()
export class CommentService {
  constructor(
    @InjectRepository(Comment) private commentRepository: Repository<Comment>,
  ) {}

  async createComment(user: User, feed: Feed, memo: string) {
    const comment = this.commentRepository.create({
      author: user,
      feed: feed,
      memo: memo,
    });
    await this.commentRepository.save(comment);
  }

  async getCommentById(id: number) {
    return await this.commentRepository.findOne({ where: { id } });
  }

  async getCommentWithAuthorAndTime(id: number) {
    return await this.commentRepository.findOne({
      relations: ['author'],
      where: { id },
      select: ['id', 'memo', 'author', 'updatedAt'],
    });
  }

  async deleteComment(id: number) {
    await this.commentRepository.softDelete(id);
  }
}
