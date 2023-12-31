import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { CommentModule } from 'src/comment/comment.module';
import { ContentModule } from 'src/content/content.module';
import { EventMemberModule } from 'src/event-member/event-member.module';
import { UserModule } from 'src/user/user.module';
import { Feed } from './entities/feed.entity';
import { FeedContent } from './entities/feedContent.entity';
import { FeedController } from './feed.controller';
import { FeedService } from './feed.service';

@Module({
  imports: [
    TypeOrmModule.forFeature([Feed, FeedContent]),
    ContentModule,
    EventMemberModule,
    CommentModule,
    UserModule,
  ],
  controllers: [FeedController],
  providers: [FeedService],
  exports: [FeedService],
})
export class FeedModule {}
