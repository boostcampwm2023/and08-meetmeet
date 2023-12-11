import { Entity, JoinColumn, ManyToOne, OneToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Detail } from 'src/detail/entities/detail.entity';
import { Event } from 'src/event/entities/event.entity';
import { User } from 'src/user/entities/user.entity';
import { Authority } from './authority.entity';

@Entity()
export class EventMember extends commonEntity {
  @ManyToOne(() => Event, (event) => event.eventMembers, { nullable: false })
  event: Event;

  @ManyToOne(() => User, { nullable: false, eager: true })
  user: User;

  @OneToOne(() => Detail, {
    nullable: false,
    eager: true,
  })
  @JoinColumn()
  detail: Detail;

  @ManyToOne(() => Authority, { nullable: false, eager: true })
  authority: Authority;
}
