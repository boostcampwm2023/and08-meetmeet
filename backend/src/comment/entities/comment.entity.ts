import { Column, Entity, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Feed } from 'src/feed/entities/feed.entity';
import { User } from 'src/user/entities/user.entity';

@Entity()
export class Comment extends commonEntity {
  @ManyToOne(() => Feed, { nullable: false, onDelete: 'CASCADE' })
  feed: Feed;

  @ManyToOne(() => User, { nullable: false })
  author: User;

  @Column({ type: 'varchar', length: 64 })
  memo: string;
}
