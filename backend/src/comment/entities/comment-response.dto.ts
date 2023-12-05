import { ApiProperty } from '@nestjs/swagger';
import { UserResponse } from 'src/user/dto/user-response';
import { Comment } from './comment.entity';

export class CommentResponseDto {
  @ApiProperty()
  id: number;
  @ApiProperty()
  author: UserResponse;
  @ApiProperty()
  memo: string;
  @ApiProperty()
  updatedAt: string;

  static of(comment: Comment): CommentResponseDto {
    return {
      id: comment.id,
      memo: comment.memo,
      author: UserResponse.from(comment.author),
      updatedAt: comment.updatedAt.toISOString(),
    };
  }
}
