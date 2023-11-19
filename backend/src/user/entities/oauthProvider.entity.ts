import { Column, Entity, OneToMany } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { User } from './user.entity';

@Entity()
export class OauthProvider extends commonEntity {
  @Column({ type: 'varchar', length: 16 })
  displayName: string;

  @OneToMany(() => User, (user) => user.oauthProvider)
  users: User[];
}
