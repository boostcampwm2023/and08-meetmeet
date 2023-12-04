import { HttpException, HttpStatus } from '@nestjs/common';

export class NoSuchOauthProviderException extends HttpException {
  constructor() {
    super('존재하지 않는 OAuth Provider입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class DuplicatedEmailException extends HttpException {
  constructor() {
    super('중복된 이메일입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class DuplicatedNicknameException extends HttpException {
  constructor() {
    super('중복된 닉네임입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class UserNotFoundException extends HttpException {
  constructor() {
    super('존재하지 않는 사용자입니다.', HttpStatus.NOT_FOUND);
  }
}
