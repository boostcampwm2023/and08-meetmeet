import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';
import { StatusEnum } from './status.enum';

@Entity()
export class Status {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ type: 'enum', enum: StatusEnum, default: StatusEnum.Pending })
  displayName: StatusEnum;
}
