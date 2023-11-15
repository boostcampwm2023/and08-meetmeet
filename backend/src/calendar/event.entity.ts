export interface ICalendar {
  id: number;
  userId?: number;
  groupId?: number;
}

export interface IEvent {
  id: number;
  calendarId?: number; // TODO: user 생성 후 수정
  title: string;
  startDate: Date;
  endDate: Date;
  isJoinable: boolean;
  isVisible?: boolean;
  memo?: string;
  announcement?: string;
  repeatPolicyId?: number; // TODO: repeatPolicy 테이블 생성
}
