import {
  Column,
  CreateDateColumn,
  DeleteDateColumn,
  Entity,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';

@Entity()
export class EventEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column('varchar', { length: 64 })
  title: string;

  @Column()
  startDate: Date;

  @Column()
  endDate: Date;

  @Column()
  isJoinable: boolean;

  @Column('varchar', { length: 255 })
  announcement: string;

  // @ManyToOne()
  // repeatPolicy_id: number;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;

  @DeleteDateColumn()
  deletedAt: Date;
}
