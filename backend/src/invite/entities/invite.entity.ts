import { Entity, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Event } from 'src/event/entities/event.entity';
import { User } from 'src/user/entities/user.entity';
import { InviteType } from './inviteType.entity';

@Entity()
export class Invite extends commonEntity {
  @ManyToOne(() => InviteType, { nullable: false })
  inviteType: InviteType;

  @ManyToOne(() => User, { nullable: false })
  sender: User;

  @ManyToOne(() => User, { nullable: false })
  receiver: User;

  @ManyToOne(() => Event, { nullable: false })
  event: Event;
}
