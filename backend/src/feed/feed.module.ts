import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ContentModule } from 'src/content/content.module';
import { EventMemberModule } from 'src/event-member/event-member.module';
import { Feed } from './entities/feed.entity';
import { FeedContent } from './entities/feedContent.entity';
import { FeedController } from './feed.controller';
import { FeedService } from './feed.service';

@Module({
  imports: [
    TypeOrmModule.forFeature([Feed, FeedContent]),
    ContentModule,
    EventMemberModule,
  ],
  controllers: [FeedController],
  providers: [FeedService],
})
export class FeedModule {}
