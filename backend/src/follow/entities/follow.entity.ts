import { Entity, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { User } from 'src/user/entities/user.entity';

@Entity()
export class Follow extends commonEntity {
  @ManyToOne(() => User, { nullable: false })
  user: User;

  @ManyToOne(() => User, { nullable: false })
  follower: User;
}
