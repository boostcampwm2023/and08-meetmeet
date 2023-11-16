import { Entity, JoinColumn, OneToMany, OneToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { User } from 'src/user/entities/user.entity';
import { Group } from 'src/group/entities/group.entity';
import { Event } from 'src/event/entities/event.entity';

@Entity()
export class Calendar extends commonEntity {
  @OneToOne(() => User)
  @JoinColumn()
  user: User;

  @OneToOne(() => Group)
  @JoinColumn()
  group: Group;

  @OneToMany(() => Event, (event) => event.calendar)
  events: Event[];
}
