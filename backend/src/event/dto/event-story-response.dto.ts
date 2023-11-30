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
  memo: string;

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
  announcement: string;
  @ApiProperty()
  repeatPolicyId: number;
  @ApiProperty()
  isVisible: boolean;
  @ApiProperty()
  memo: string;
  @ApiProperty({ type: EventFeed })
  feeds: EventFeed[];

  static of(event: Event, userId: number): EventStoryResponseDto {
    const memberDetail = event.eventMembers.find(
      (eventMember) => eventMember.user.id === userId,
    );
    if (!memberDetail) {
      throw new Error(
        `Cannot find eventmember where eventId=${event.id} and userId=${userId}`,
      );
    }

    return {
      id: event.id,
      title: event.title,
      startDate: event.startDate.toISOString(),
      endDate: event.endDate.toISOString(),
      eventMembers: event.eventMembers.map((eventMember) =>
        Member.of(eventMember),
      ),
      announcement: event.announcement,
      authority: memberDetail.authority.displayName ?? null,
      repeatPolicyId: event.repeatPolicyId,
      isJoinable: event.isJoinable ? true : false,
      isVisible: memberDetail.detail.isVisible ? true : false,
      memo: memberDetail.detail.memo,
      feeds: event.feeds.map((feed) => EventFeed.of(feed)),
    };
  }
}
