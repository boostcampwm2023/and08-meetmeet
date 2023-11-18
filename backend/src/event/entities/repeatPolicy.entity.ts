import { Column, Entity, OneToMany } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Event } from './event.entity';

@Entity()
export class RepeatPolicy extends commonEntity {
  @Column({ type: 'timestamp', default: () => 'CURRENT_TIMESTAMP' })
  startDate: Date;

  @Column('timestamp', {
    default: () => "'2038-01-18 00:00:00'",
  })
  endDate: Date;

  @Column({ type: 'int', nullable: true })
  repeatDay: number;

  @Column({ type: 'int', nullable: true })
  repeatWeek: number;

  @Column({ type: 'int', nullable: true })
  repeatMonth: number;

  @Column({ type: 'int', nullable: true })
  repeatYear: number;

  @OneToMany(() => Event, (event) => event.repeatPolicy)
  events: Event[];
}
