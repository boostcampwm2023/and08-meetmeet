import { Column, Entity } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';

@Entity()
export class Detail extends commonEntity {
  @Column({ type: 'tinyint', nullable: true, default: 1 })
  isVisible: boolean;

  @Column({ type: 'varchar', length: 64, nullable: true })
  memo: string;
}
