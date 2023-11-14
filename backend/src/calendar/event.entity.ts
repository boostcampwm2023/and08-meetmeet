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
  isAllday: boolean;
  isJoin: boolean;
  isVisible?: boolean;
  memo?: string;
  announcement?: string;
  repeatePolicyId?: number;
}
