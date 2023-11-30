import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  UploadedFiles,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { FilesInterceptor } from '@nestjs/platform-express';
import {
  ApiBearerAuth,
  ApiBody,
  ApiConsumes,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { GetUser } from 'src/auth/get-user.decorator';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import { User } from 'src/user/entities/user.entity';
import { CreateFeedDto } from './dto/create-feed.dto';
import { FeedService } from './feed.service';

@ApiTags('feed')
@Controller('feed')
export class FeedController {
  constructor(private readonly feedService: FeedService) {}

  @UseGuards(JwtAuthGuard)
  @UseInterceptors(FilesInterceptor('contents', 10))
  @Post()
  @ApiOperation({
    summary: '피드 생성 API',
    description: '일정 멤버만 피드를 생성할 수 있습니다.',
  })
  @ApiBearerAuth()
  @ApiConsumes('multipart/form-data')
  createFeed(
    @GetUser() user: User,
    @UploadedFiles() contents: Array<Express.Multer.File>,
    @Body() createFeedDto: CreateFeedDto,
  ) {
    return this.feedService.createFeed(user, contents, createFeedDto);
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/comment')
  @ApiOperation({ summary: '댓글 작성 API' })
  @ApiBearerAuth()
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        memo: {
          type: 'string',
          description: '댓글 내용',
        },
      },
    },
  })
  createComment(
    @GetUser() user: User,
    @Param('id') id: number,
    @Body('memo') memo: string,
  ) {
    return this.feedService.createComment(user, id, memo);
  }

  @UseGuards(JwtAuthGuard)
  // @HttpCode(204)
  @Delete(':feedId/comment/:commentId')
  @ApiOperation({ summary: '댓글 삭제 API' })
  @ApiBearerAuth()
  deleteComment(
    @GetUser() user: User,
    @Param('feedId') feedId: number,
    @Param('commentId') commentId: number,
  ) {
    return this.feedService.deleteComment(user, feedId, commentId);
  }

  @UseGuards(JwtAuthGuard)
  @Get(':id')
  @ApiOperation({
    summary: '피드 조회 API',
    description: '피드를 조회한다.',
  })
  @ApiBearerAuth()
  getFeed(@Param('id') id: number) {
    return this.feedService.getFeed(id);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id')
  @ApiOperation({
    summary: '피드 삭제 API',
    description: '일정 주인 또는 피드 작성자는 피드를 삭제할 수 있다.',
  })
  @ApiBearerAuth()
  deleteFeed(@GetUser() user: User, @Param('id') id: number) {
    return this.feedService.deleteFeed(user, id);
  }
}
