import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { ContentService } from 'src/content/content.service';
import { EventMemberService } from 'src/event-member/event-member.service';
import { User } from 'src/user/entities/user.entity';
import { Repository } from 'typeorm';
import { CreateFeedDto } from './dto/create-feed.dto';
import { FeedResponseDto } from './dto/feed-response.dto';
import { Feed } from './entities/feed.entity';
import { FeedContent } from './entities/feedContent.entity';

@Injectable()
export class FeedService {
  constructor(
    @InjectRepository(Feed) private feedRepository: Repository<Feed>,
    @InjectRepository(FeedContent)
    private feedContentRepository: Repository<FeedContent>,
    private readonly contentService: ContentService,
    private readonly eventMemberSercive: EventMemberService,
  ) {}

  async createFeed(
    user: User,
    files: Array<Express.Multer.File>,
    createFeedDto: CreateFeedDto,
  ) {
    if (!files && !createFeedDto.memo) {
      throw new BadRequestException('사진/영상/글을 작성해주세요.');
    }

    const authority = await this.eventMemberSercive.getAuthorityOfUserByEventId(
      createFeedDto.eventId,
      user.id,
    );

    if (!authority) {
      throw new BadRequestException('일정 멤버만 피드를 추가할 수 있습니다.');
    }

    const feed = this.feedRepository.create({ ...createFeedDto, author: user });
    const contents = await this.contentService.createContentBulk(files);
    await this.feedRepository.save(feed);

    const feedContents = contents.reduce((acc, content) => {
      return [...acc, this.feedContentRepository.create({ feed, content })];
    }, []);

    await this.feedContentRepository.insert(feedContents);
  }

  async getFeed(id: number) {
    const feed = await this.feedRepository
      .createQueryBuilder('feed')
      .select([
        'feed.id',
        'feed.memo',
        'feed.author',
        'fc.contentId',
        'fc.content',
        // 'feed.comments',
      ])
      .leftJoin('feed.feedContents', 'fc')
      // .leftJoinAndSelect('feed.comments', 'comment')
      .leftJoinAndSelect('feed.author', 'author')
      .leftJoinAndSelect('fc.content', 'content')
      .where('feed.id = :id', { id })
      .getOne();

    if (!feed) {
      throw new NotFoundException();
    }

    return FeedResponseDto.of(feed);
  }

  async deleteFeed(user: User, id: number) {}
}
