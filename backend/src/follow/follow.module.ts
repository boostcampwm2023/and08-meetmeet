import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Follow } from './entities/follow.entity';
import { FollowController } from './follow.controller';
import { FollowService } from './follow.service';
import { User } from '../user/entities/user.entity';
import { InviteModule } from '../invite/invite.module';

@Module({
  imports: [TypeOrmModule.forFeature([Follow, User]), InviteModule],
  controllers: [FollowController],
  providers: [FollowService],
  exports: [FollowService],
})
export class FollowModule {}
