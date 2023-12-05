import { Entity, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Event } from 'src/event/entities/event.entity';
import { User } from 'src/user/entities/user.entity';
import { Status } from './status.entity';

@Entity()
export class Invite extends commonEntity {
  @ManyToOne(() => Status, { nullable: false, eager: true })
  status: Status;

  @ManyToOne(() => User, { nullable: false })
  sender: User;

  @ManyToOne(() => User, { nullable: false })
  receiver: User;

  @ManyToOne(() => Event, { nullable: true })
  event: Event;
}
