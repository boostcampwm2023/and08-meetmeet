import { Content } from 'src/content/entities/content.entity';
import { Entity, ManyToOne, PrimaryColumn } from 'typeorm';
import { Feed } from './feed.entity';

@Entity()
export class FeedContent {
  @PrimaryColumn()
  feedId: number;

  @PrimaryColumn()
  contentId: number;

  @ManyToOne(() => Feed, { nullable: false })
  feed: Feed;

  @ManyToOne(() => Content, { nullable: false })
  content: Content;
}
