import { Module } from '@nestjs/common';
import { DetailService } from './detail.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Detail } from './entities/detail.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Detail])],
  providers: [DetailService],
  exports: [DetailService],
})
export class DetailModule {}
