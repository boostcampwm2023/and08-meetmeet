import { HttpException, HttpStatus } from '@nestjs/common';

export class NotExistInviteException extends HttpException {
  constructor() {
    super('존재하지 않는 초대입니다.', HttpStatus.NOT_FOUND);
  }
}

export class InvalidInviteException extends HttpException {
  constructor() {
    super('유효하지 않은 초대입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class NotExistStatusException extends HttpException {
  constructor() {
    super('존재하지 않는 초대 상태입니다.', HttpStatus.NOT_FOUND);
  }
}
