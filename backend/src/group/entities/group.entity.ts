import { Column, Entity, JoinColumn, ManyToOne } from 'typeorm';
import { commonEntity } from 'src/common/common.entity';
import { Content } from 'src/content/entities/content.entity';

@Entity()
export class Group extends commonEntity {
  @Column({ type: 'varchar', length: 255 })
  groupName: string;

  @ManyToOne(() => Content)
  @JoinColumn()
  groupImage: Content;
}
