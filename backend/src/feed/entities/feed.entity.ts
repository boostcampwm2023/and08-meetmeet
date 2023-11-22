import { Column, Entity, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Event } from 'src/event/entities/event.entity';
import { User } from 'src/user/entities/user.entity';

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
}
