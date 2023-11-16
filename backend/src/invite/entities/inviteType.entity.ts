import { Column, Entity } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';

@Entity()
export class InviteType extends commonEntity {
  @Column({ type: 'varchar', length: 16 })
  displayName: string;

  @Column({ type: 'varchar', length: 16 })
  requestType: string;
}
