import { ApiProperty } from '@nestjs/swagger';
import { Feed } from 'src/feed/entities/feed.entity';
import { Event } from '../entities/event.entity';
import { EventResponse, Member } from './event-response';

class EventFeed {
  @ApiProperty()
  id: number;
  @ApiProperty()
  thumbnail: string | null;
  @ApiProperty()
  memo: string | null;

  static of(feed: Feed): EventFeed {
    return {
      id: feed.id,
      // TODO: thumbnail
      thumbnail: feed.feedContents[0]?.content?.path ?? null,
      memo: feed.memo ?? null,
    };
  }
}

export class EventStoryResponseDto extends EventResponse {
  @ApiProperty()
  announcement: string | null;
  @ApiProperty()
  repeatPolicyId: number | null;
  @ApiProperty({ type: EventFeed })
  feeds: EventFeed[];

  static of(event: Event, userId: number): EventStoryResponseDto {
    const memberDetail = event.eventMembers.find(
      (eventMember) => eventMember.user?.id === userId,
    );

    return {
      id: event.id,
      title: event.title,
      startDate: event.startDate.toISOString(),
      endDate: event.endDate.toISOString(),
      eventMembers: event.eventMembers.map((eventMember) =>
        Member.of(eventMember),
      ),
      announcement: event.announcement ?? null,
      authority: memberDetail?.authority.displayName ?? null,
      repeatPolicyId: event.repeatPolicyId ?? null,
      isJoinable: event.isJoinable ? true : false,
      feeds: event.feeds.map((feed) => EventFeed.of(feed)),
    };
  }
}
