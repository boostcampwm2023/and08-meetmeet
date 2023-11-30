import { Column, Entity } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';

@Entity()
export class Content extends commonEntity {
  @Column({ type: 'varchar', length: 8 })
  type: string;

  @Column({ type: 'varchar', length: 16 })
  mimeType: string;

  @Column({ type: 'varchar', length: 64, nullable: true })
  thumbnail: string;

  @Column({ type: 'varchar', length: 255 })
  path: string;
}
