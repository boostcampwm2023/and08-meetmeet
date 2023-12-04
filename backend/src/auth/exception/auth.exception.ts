import { HttpException, HttpStatus } from '@nestjs/common';

export class InvalidTokenException extends HttpException {
  constructor() {
    super('유효하지 않은 토큰입니다.', HttpStatus.I_AM_A_TEAPOT);
  }
}

export class ExpiredTokenException extends HttpException {
  constructor() {
    super('만료된 토큰입니다.', HttpStatus.I_AM_A_TEAPOT);
  }
}

export class InvalidPayloadException extends HttpException {
  constructor() {
    super('토큰 정보가 올바르지 않습니다.', HttpStatus.I_AM_A_TEAPOT);
  }
}

export class InvalidUserException extends HttpException {
  constructor() {
    super('email, password를 확인해주세요.', HttpStatus.BAD_REQUEST);
  }
}

export class DeletedUserException extends HttpException {
  constructor() {
    super('탈퇴한 사용자입니다.', HttpStatus.BAD_REQUEST);
  }
}
