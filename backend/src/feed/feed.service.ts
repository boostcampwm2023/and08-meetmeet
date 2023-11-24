import {
  BadRequestException,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { ContentService } from 'src/content/content.service';
import { AuthorityEnum } from 'src/event-member/entities/authority.enum';
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
        'comment',
      ])
      .leftJoin('feed.feedContents', 'fc')
      .leftJoin('feed.comments', 'comment')
      .leftJoinAndSelect('feed.author', 'author')
      .leftJoinAndSelect('fc.content', 'content')
      .where('feed.id = :id', { id })
      .getOne();

    if (!feed) {
      throw new NotFoundException();
    }

    return FeedResponseDto.of(feed);
  }

  async deleteFeed(user: User, id: number) {
    const feed = await this.feedRepository
      .createQueryBuilder('feed')
      .select([
        'feed.id',
        'feed.authorId',
        'feed.memo',
        'fc.contentId',
        'comment',
      ])
      .leftJoin('feed.feedContents', 'fc')
      .leftJoin('feed.comments', 'comment')
      .leftJoin('fc.content', 'content')
      .where('feed.id = :id', { id })
      .getOne();

    if (!feed) {
      throw new NotFoundException();
    }

    if (feed.author.id !== user.id) {
      const authority =
        await this.eventMemberSercive.getAuthorityOfUserByEventId(
          feed.eventId,
          user.id,
        );

      if (authority !== AuthorityEnum.OWNER) {
        throw new UnauthorizedException('피드 삭제 권한이 없습니다.');
      }
    }

    if (feed.feedContents.length) {
      this.contentService.softDeleteContent(
        feed.feedContents.map((feedContent) => feedContent.contentId),
      );
    }

    this.feedRepository.softRemove(feed);
  }
}
