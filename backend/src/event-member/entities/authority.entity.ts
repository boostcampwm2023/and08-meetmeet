import { Column, Entity } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { AuthorityEnum } from './authority.enum';

@Entity()
export class Authority extends commonEntity {
  @Column({ type: 'enum', enum: AuthorityEnum, default: AuthorityEnum.MEMBER })
  displayName: string;
}
