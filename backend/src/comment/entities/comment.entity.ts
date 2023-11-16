import { Column, Entity, JoinColumn, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Feed } from 'src/feed/entities/feed.entity';
import { User } from 'src/user/entities/user.entity';

@Entity()
export class Comment extends commonEntity {
  @ManyToOne(() => Feed, { nullable: false })
  feed: Feed;

  @ManyToOne(() => User, { nullable: false })
  @JoinColumn()
  author: User;

  @Column({ type: 'varchar', length: 64 })
  memo: string;
}
