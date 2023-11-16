import { Column, Entity, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Calendar } from '../../calendar/entities/calendar.entity';

@Entity()
export class Event extends commonEntity {
  @ManyToOne(() => Calendar, (calendar) => calendar.events)
  calendar: Calendar;

  @Column({ type: 'varchar', length: 64 })
  title: string;

  @Column({ type: 'timestamp' })
  startDate: Date;

  @Column({ type: 'timestamp' })
  endDate: Date;

  @Column({ type: 'tinyint', default: 1 })
  isJoinable: boolean;

  @Column({ type: 'varchar', length: 255, nullable: true })
  announcement: string;

  @Column({ type: 'int', nullable: true })
  // @OneToOne(() => RepeatPolicy, {nullable: true})
  repeatPolicy: number;
}
