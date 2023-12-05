import { HttpException } from '@nestjs/common';

export class NoSuchUserException extends HttpException {
  constructor() {
    super('존재하지 않는 사용자입니다.', 400);
  }
}

export class FollowSelfException extends HttpException {
  constructor() {
    super('자기 자신은 팔로우할 수 없습니다.', 400);
  }
}

export class AlreadyFollowException extends HttpException {
  constructor() {
    super('이미 팔로우한 사용자입니다.', 400);
  }
}

export class NotFollowingException extends HttpException {
  constructor() {
    super('팔로우하고 있지 않습니다.', 400);
  }
}
