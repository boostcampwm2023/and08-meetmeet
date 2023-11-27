import { Comment } from 'src/comment/entities/comment.entity';
import { Content } from 'src/content/entities/content.entity';
import { User } from 'src/user/entities/user.entity';
import { Feed } from '../entities/feed.entity';

export class FeedResponseDto {
  id: number;
  author: User;
  memo: string;
  contents: Content[];
  comments: Comment[];

  static of(feed: Feed): FeedResponseDto {
    return {
      id: feed.id,
      memo: feed.memo ?? null,
      author: feed.author,
      contents: feed.feedContents.map((feedContent) => feedContent.content),
      comments: feed.comments, // TODO: comment 추가
    };
  }
}
