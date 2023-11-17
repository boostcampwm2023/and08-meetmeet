import { Entity, ManyToOne, PrimaryColumn } from 'typeorm';
import { Authority } from 'src/event-member/entities/autority.entity';
import { Group } from 'src/group/entities/group.entity';
import { User } from './user.entity';

@Entity()
export class UserGroup {
  @PrimaryColumn()
  userId: number;

  @PrimaryColumn()
  groupId: number;

  @ManyToOne(() => User, { nullable: false })
  user: User;

  @ManyToOne(() => Group, { nullable: false })
  group: Group;

  @ManyToOne(() => Authority, { nullable: false })
  authority: Authority;
}
