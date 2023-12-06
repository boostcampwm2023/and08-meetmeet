import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { CommentService } from 'src/comment/comment.service';
import { ContentService } from 'src/content/content.service';
import { AuthorityEnum } from 'src/event-member/entities/authority.enum';
import { EventMemberService } from 'src/event-member/event-member.service';
import { User } from 'src/user/entities/user.entity';
import { Repository } from 'typeorm';
import { CreateFeedDto } from './dto/create-feed.dto';
import { FeedResponseDto } from './dto/feed-response.dto';
import { Feed } from './entities/feed.entity';
import { FeedContent } from './entities/feedContent.entity';
import {
  CommentForbiddenException,
  CommentNotFoundException,
  EmptyFeedRequestException,
  FeedForbiddenException,
  FeedNotFoundException,
} from './exception/feed.exception';

@Injectable()
export class FeedService {
  constructor(
    @InjectRepository(Feed) private feedRepository: Repository<Feed>,
    @InjectRepository(FeedContent)
    private feedContentRepository: Repository<FeedContent>,
    private readonly contentService: ContentService,
    private readonly eventMemberService: EventMemberService,
    private readonly commentService: CommentService,
  ) {}

  async createFeed(
    user: User,
    files: Array<Express.Multer.File>,
    createFeedDto: CreateFeedDto,
  ) {
    if (!files && !createFeedDto.memo) {
      throw new EmptyFeedRequestException();
    }

    const authority = await this.eventMemberService.getAuthorityOfUserByEventId(
      createFeedDto.eventId,
      user.id,
    );

    if (!authority) {
      throw new FeedForbiddenException();
    }

    const feed = this.feedRepository.create({ ...createFeedDto, author: user });
    const contents = await this.contentService.createContentBulk(
      files,
      `event/${createFeedDto.eventId}`,
    );
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
        'author.profile',
        'fc.contentId',
        'fc.content',
        'comment',
      ])
      .leftJoin('feed.feedContents', 'fc')
      .leftJoin('feed.comments', 'comment')
      .leftJoinAndSelect('feed.author', 'author')
      .leftJoinAndSelect('author.profile', 'profile')
      .leftJoinAndSelect('fc.content', 'content')
      .where('feed.id = :id', { id })
      .getOne();

    if (!feed) {
      throw new FeedNotFoundException();
    }

    await Promise.all(
      feed.comments.map(async (comment) => {
        const result = await this.commentService.getCommentWithAuthorAndTime(
          comment.id,
        );
        if (!result) {
          throw new CommentNotFoundException();
        }

        comment.author = result.author;
        comment.updatedAt = result.updatedAt;
      }),
    ).catch((err) => {
      throw err;
    });

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
      throw new FeedNotFoundException();
    }

    if (feed.authorId !== user.id) {
      const authority =
        await this.eventMemberService.getAuthorityOfUserByEventId(
          feed.eventId,
          user.id,
        );

      if (authority !== AuthorityEnum.OWNER) {
        throw new FeedForbiddenException();
      }
    }

    if (feed.feedContents.length) {
      this.contentService.softDeleteContent(
        feed.feedContents.map((feedContent) => feedContent.contentId),
      );
    }

    await this.commentService.deleteComments(
      feed.comments.map((comment) => comment.id),
    );
    await this.feedRepository.softDelete(feed.id);
  }

  async createComment(user: User, feedId: number, memo: string) {
    const feed = await this.feedRepository.findOne({ where: { id: feedId } });
    if (!feed) {
      throw new FeedNotFoundException();
    }

    if (
      !(await this.eventMemberService.getAuthorityOfUserByEventId(
        feed.eventId,
        user.id,
      ))
    ) {
      throw new CommentForbiddenException();
    }

    await this.commentService.createComment(user, feed, memo);
  }

  async deleteComment(user: User, feedId: number, commentId: number) {
    const comment = await this.commentService.getCommentById(commentId);
    const feed = await this.feedRepository.findOne({ where: { id: feedId } });

    if (!comment || !feed) {
      throw new CommentNotFoundException();
    }

    if (comment.authorId !== user.id) {
      const authority =
        await this.eventMemberService.getAuthorityOfUserByEventId(
          feed.eventId,
          user.id,
        );

      if (!authority || authority !== 'OWNER') {
        throw new CommentForbiddenException();
      }
    }

    await this.commentService.deleteComment(commentId);
  }
}
