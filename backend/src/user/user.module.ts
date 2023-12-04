import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { OauthProvider } from './entities/oauthProvider.entity';
import { User } from './entities/user.entity';
import { UserService } from './user.service';
import { UserController } from './user.controller';
import { ContentModule } from 'src/content/content.module';
import { FollowModule } from '../follow/follow.module';
import { InviteModule } from '../invite/invite.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([User, OauthProvider]),
    ContentModule,
    FollowModule,
    InviteModule,
  ],
  providers: [UserService],
  controllers: [UserController],
  exports: [UserService],
})
export class UserModule {}
