import { CommentResponseDto } from 'src/comment/entities/comment-response.dto';
import { Content } from 'src/content/entities/content.entity';
import { UserResponse } from 'src/user/dto/user-response';
import { Feed } from '../entities/feed.entity';

export class FeedResponseDto {
  id: number;
  author: UserResponse;
  memo: string;
  contents: Content[];
  comments: CommentResponseDto[];

  static of(feed: Feed): FeedResponseDto {
    return {
      id: feed.id,
      memo: feed.memo ?? null,
      author: UserResponse.from(feed.author),
      contents: feed.feedContents.map((feedContent) => feedContent.content),
      comments: feed.comments.map((comment) => CommentResponseDto.of(comment)),
    };
  }
}
