import { HttpException, HttpStatus } from '@nestjs/common';

export class EventNotFoundException extends HttpException {
  constructor() {
    super('존재하지 않는 이벤트입니다.', HttpStatus.NOT_FOUND);
  }
}

export class EventPropertyNotFoundException extends HttpException {
  constructor() {
    super('일정 속성을 전부 입력해주세요.', HttpStatus.NOT_FOUND);
  }
}

export class EventDetailNotFoundException extends HttpException {
  constructor() {
    super('일정 내용을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
  }
}

export class NotEventMemberException extends HttpException {
  constructor() {
    super('이벤트에 참여하지 않았습니다.', HttpStatus.BAD_REQUEST);
  }
}
export class InvalidAuthorityException extends HttpException {
  constructor() {
    super('올바르지 않은 권한입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class AlreadyJoinedException extends HttpException {
  constructor() {
    super('이미 참여 중입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class InvalidRepeatPolicyException extends HttpException {
  constructor() {
    super('올바르지 않은 반복 설정입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class UpdateAllRepeatEventsException extends HttpException {
  constructor() {
    super('이후의 반복 일정을 전부 업데이트해야 합니다.', 423);
  }
}

export class EventForbiddenException extends HttpException {
  constructor() {
    super('이벤트에 권한이 없습니다.', HttpStatus.FORBIDDEN);
  }
}

export class NotRepeatEventException extends HttpException {
  constructor() {
    super('반복 일정이 아닙니다.', HttpStatus.BAD_REQUEST);
  }
}

export class SearchPeriodException extends HttpException {
  constructor() {
    super('일정 검색 최대 기간은 6개월입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class SearchSelfException extends HttpException {
  constructor() {
    super('자기 자신은 검색할 수 없습니다.', HttpStatus.BAD_REQUEST);
  }
}

export class InviteSelfException extends HttpException {
  constructor() {
    super('자기 자신은 초대할 수 없습니다.', HttpStatus.BAD_REQUEST);
  }
}

export class InviteNotFoundException extends HttpException {
  constructor() {
    super('존재하지 않는 초대입니다.', HttpStatus.NOT_FOUND);
  }
}

export class ExpiredInviteException extends HttpException {
  constructor() {
    super('만료된 초대입니다.', HttpStatus.BAD_REQUEST);
  }
}

export class NotInviteReceiverException extends HttpException {
  constructor() {
    super('초대 받은 사람이 아닙니다.', HttpStatus.BAD_REQUEST);
  }
}

export class createEventFailException extends HttpException {
  constructor() {
    super(
      '이벤트 생성 중 오류가 발생했습니다.',
      HttpStatus.INTERNAL_SERVER_ERROR,
    );
  }
}

export class updateEventFailException extends HttpException {
  constructor() {
    super(
      '이벤트 수정 중 오류가 발생했습니다.',
      HttpStatus.INTERNAL_SERVER_ERROR,
    );
  }
}

export class deleteEventFailException extends HttpException {
  constructor() {
    super(
      '이벤트 삭제 중 오류가 발생했습니다.',
      HttpStatus.INTERNAL_SERVER_ERROR,
    );
  }
}

export class NotJoinEventException extends HttpException {
  constructor() {
    super('참여할 수 없는 이벤트입니다.', HttpStatus.BAD_REQUEST);
  }
}
