import { commonEntity } from 'src/common/common.entity';
import { Column, Entity, OneToMany } from 'typeorm';
import { Event } from '../../event/entities/event.entity';

@Entity()
export class Calendar extends commonEntity {
  @Column({ type: 'int', nullable: true })
  // @OneToOne(() => User, {nullable: true})
  userId: number;

  @Column({ type: 'int', nullable: true })
  groupId: number;

  @OneToMany(() => Event, (event) => event.calendar)
  events: Event[];
}
