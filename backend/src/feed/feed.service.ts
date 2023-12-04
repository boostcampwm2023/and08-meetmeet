import {
  BadRequestException,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { CommentService } from 'src/comment/comment.service';
import { ContentService } from 'src/content/content.service';
import { AuthorityEnum } from 'src/event-member/entities/authority.enum';
import { EventMemberService } from 'src/event-member/event-member.service';
import { User } from 'src/user/entities/user.entity';
import { UserService } from 'src/user/user.service';
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
    private readonly eventMemberService: EventMemberService,
    private readonly commentService: CommentService,
    private readonly userService: UserService,
  ) {}

  async createFeed(
    user: User,
    files: Array<Express.Multer.File>,
    createFeedDto: CreateFeedDto,
  ) {
    if (!files && !createFeedDto.memo) {
      throw new BadRequestException('사진/영상/글을 작성해주세요.');
    }

    const authority = await this.eventMemberService.getAuthorityOfUserByEventId(
      createFeedDto.eventId,
      user.id,
    );

    if (!authority) {
      throw new BadRequestException('일정 멤버만 피드를 추가할 수 있습니다.');
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

    await Promise.all(
      feed.comments.map(async (comment) => {
        const result = await this.commentService.getCommentWithAuthorAndTime(
          comment.id,
        );
        if (!result) {
          throw new NotFoundException(`Not found comment id=${comment.id}`);
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
      throw new NotFoundException();
    }

    if (feed.authorId !== user.id) {
      const authority =
        await this.eventMemberService.getAuthorityOfUserByEventId(
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

    await this.feedRepository.softRemove(feed);
  }

  async createComment(user: User, feedId: number, memo: string) {
    const feed = await this.feedRepository.findOne({ where: { id: feedId } });
    if (!feed) {
      throw new BadRequestException(`There is no feed where id = ${feedId}`);
    }

    if (
      !(await this.eventMemberService.getAuthorityOfUserByEventId(
        feed.eventId,
        user.id,
      ))
    ) {
      throw new UnauthorizedException(`일정 멤버만 댓글을 작성할 수 있습니다.`);
    }

    await this.commentService.createComment(user, feed, memo);
  }

  async deleteComment(user: User, feedId: number, commentId: number) {
    const comment = await this.commentService.getCommentById(commentId);
    const feed = await this.feedRepository.findOne({ where: { id: feedId } });

    if (!comment || !feed) {
      throw new NotFoundException('Comment Not Found');
    }

    if (comment.authorId !== user.id) {
      const authority =
        await this.eventMemberService.getAuthorityOfUserByEventId(
          feed.eventId,
          user.id,
        );

      if (!authority || authority !== 'OWNER') {
        throw new UnauthorizedException(`댓글 삭제 권한이 없습니다.`);
      }
    }

    await this.commentService.deleteComment(commentId);
  }
}
