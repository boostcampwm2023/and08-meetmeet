import { UserResponse } from 'src/user/dto/user-response';
import { Comment } from './comment.entity';

export class CommentResponseDto {
  id: number;
  author: UserResponse;
  memo: string;

  static of(comment: Comment): CommentResponseDto {
    return {
      id: comment.id,
      memo: comment.memo,
      author: UserResponse.from(comment.author),
    };
  }
}
