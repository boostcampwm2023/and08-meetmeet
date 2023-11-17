import { Column, Entity } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';

@Entity()
export class Authority extends commonEntity {
  @Column({ type: 'varchar', length: 255 })
  displayName: string;
}
