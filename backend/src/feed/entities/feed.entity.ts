import { Column, Entity, ManyToOne, OneToMany, OneToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Event } from 'src/event/entities/event.entity';
import { User } from 'src/user/entities/user.entity';
import { FeedContent } from './feedContent.entity';
import { Comment } from '../../comment/entities/comment.entity';

@Entity()
export class Feed extends commonEntity {
  @Column()
  eventId: number;

  @ManyToOne(() => User, { nullable: false })
  author: User;

  @ManyToOne(() => Event, { nullable: false })
  event: Event;

  @Column({ type: 'varchar', length: 64, nullable: true, default: null })
  memo: string;

  @OneToMany(() => FeedContent, (feedContent) => feedContent.feed)
  feedContents: FeedContent[];

  @OneToMany(() => Comment, (comment) => comment.feed)
  comments: Comment[];
}
