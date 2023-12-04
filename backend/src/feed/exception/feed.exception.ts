import { HttpException, HttpStatus } from '@nestjs/common';

export class EmptyFeedRequestException extends HttpException {
  constructor() {
    super('사진/영상/글을 작성해주세요.', HttpStatus.BAD_REQUEST);
  }
}

export class FeedForbiddenException extends HttpException {
  constructor() {
    super('피드 권한이 없습니다.', HttpStatus.FORBIDDEN);
  }
}

export class FeedNotFoundException extends HttpException {
  constructor() {
    super('존재하지 않는 피드입니다.', HttpStatus.NOT_FOUND);
  }
}

export class CommentNotFoundException extends HttpException {
  constructor() {
    super('존재하지 않는 댓글입니다.', HttpStatus.NOT_FOUND);
  }
}

export class CommentForbiddenException extends HttpException {
  constructor() {
    super('댓글 권한이 없습니다.', HttpStatus.FORBIDDEN);
  }
}
