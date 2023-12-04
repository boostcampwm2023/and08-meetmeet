import { HttpException, HttpStatus } from '@nestjs/common';

export class ObjectStorageUploadException extends HttpException {
  constructor() {
    super(
      'Object Storage에 업로드 중 오류가 발생했습니다.',
      HttpStatus.INTERNAL_SERVER_ERROR,
    );
  }
}

export class ObjectStorageDeleteException extends HttpException {
  constructor() {
    super(
      'Object Storage에서 삭제 중 오류가 발생했습니다.',
      HttpStatus.INTERNAL_SERVER_ERROR,
    );
  }
}

export class ContentNotFoundException extends HttpException {
  constructor() {
    super('해당하는 컨텐츠를 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
  }
}
