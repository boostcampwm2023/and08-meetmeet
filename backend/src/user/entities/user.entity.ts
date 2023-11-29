import { Column, Entity, JoinColumn, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Content } from 'src/content/entities/content.entity';
import { OauthProvider } from './oauthProvider.entity';

@Entity()
export class User extends commonEntity {
  @Column({ type: 'varchar', length: 255, unique: true })
  email: string;

  @Column({ type: 'varchar', length: 255, select: false })
  password: string;

  @Column({ type: 'varchar', length: 64, unique: true })
  nickname: string;

  @Column({ nullable: true })
  profileId: number;

  @ManyToOne(() => Content, { eager: true })
  @JoinColumn()
  profile: Content;

  @ManyToOne(() => OauthProvider, { nullable: true })
  oauthProvider: OauthProvider;
}
