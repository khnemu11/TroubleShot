export interface DefaultRespense {
  success: boolean;
  message: string;
}
export interface GetMostTags extends DefaultRespense {
  tagList: string[];
}
export interface CreateOptions {
  category: string;
  scope: number | null;
  tags: string[];
  solved: boolean | null;
}
export interface PostTroubleShooting {
  title: string;
  category: string;
  context: string;
  dependency: string;
  scope: number;
  writer: {
    seq: number;
  } | null;
  solved: boolean;
  tags: string[];
  postType: 0;
}
export interface RequestTroubleShooting {
  loginSeq: number;
  type: 0;
  troubleShooting: PostTroubleShooting;
}

export interface SearchParams {
  keyword?: string;
  pageSize?: number;
  pageNo?: number;
  category?: string;
  solved?: boolean;
  tags?: string[];
  writer?: string;
  dependency?: string;
  troubleSeq?: number;
  writerSeq?: number;
  loginSeq?: number;
  startTime?: string;
  endTime?: string;
  favorite?: boolean;
  order?: number;
}
export interface Writer {
  seq: number;
  email: string;
  profileImg: string;
  nickname: string;
}
export interface TroubleShootingBoard {
  seq: number;
  createTime: string;
  updateTime: string;
  title: string;
  category: string;
  context: string;
  dependency: null | string;
  scope: number;
  writer: Writer | null;
  solved: boolean;
  viewCount: number;
  likeCount: number;
  replyCount: number;
  answerCount: number;
  tags: string[];
  replies: null | Reply[];
  answers: Answer[];
  loginLike: false;
  favorite: false;
}

export interface GetTroubleList {
  success: boolean;
  message: string;
  troubleShootingList: TroubleShootingBoard[];
  totalCount: number;
}

export interface GetTroubleDetail {
  success: boolean;
  message: string;
  troubleShooting: TroubleShootingBoard;
}
export interface Reply {
  seq: number;
  createTime: string;
  updateTime: string;
  context: string;
  writerSeq: number;
  writer: Writer | null;
  likeCount: number;
  troubleSeq: number;
  loginLike: boolean;
}
export interface Answer {
  seq: 1;
  createTime: string;
  updateTime: string;
  deleteTime: null;
  title: string;
  context: string;
  writer: Writer | null;
  likeCount: number;
  replyCount: number;
  troubleSeq: number;
  replies: Reply[];
  loginLike: boolean;
  selected: boolean;
}
export interface TroubleShootingAnswer {
  context: string;
  title: string;
  writer: {
    seq: number;
  } | null;
  troubleSeq: number;
  selected: boolean;
}
export interface TroubleShootingAnswerReq {
  context: string;
  title: string;
  writer: {
    seq: number;
  } | null;
  troubleSeq: number;
}

export interface RequestTroubleShootingAnswer {
  loginSeq: number;
  type: 0;
  troubleShootingAnswer: TroubleShootingAnswerReq;
}
export interface TroubleShootingReply {
  context: string;
  writer: {
    seq: number;
  } | null;
  troubleSeq: number;
}
export interface TroubleShootingAnswerReply {
  context: string;
  writer: {
    seq: number;
  } | null;
  answerSeq: number;
}
export interface RequestTroubleShootingReply {
  loginSeq: number;
  type: 0;
  troubleShootingReply: TroubleShootingReply;
}
export interface RequestTroubleShootingAnswerReply {
  loginSeq: number;
  type: 0;
  troubleShootingAnswerReply: TroubleShootingAnswerReply;
}
export interface ResponseCategory {
  success: boolean;
  message: string;
  categoryList: Category[];
  totalCount: number;
}
export interface Category {
  seq: number;
  createTime: string;
  updateTime: string;
  name: string;
  userSeq: number;
}
export interface SearchMember {
  pageSize?: number;
  pageNo?: number;
  nickname: string;
}

export interface ResponeseSearchMember extends DefaultRespense {
  memberList: Member[];
}

export interface Member {
  seq: number;
  createTime: string;
  updateTime: string;
  email: string;
  profileImg: string;
  nickname: string;
  locale: string;
}

export interface RequestPostAiAnswer {
  loginSeq: number;
  type: 0;
  context: string;
}
export interface ResponsePostAiAnswer extends DefaultRespense {
  context?: string;
}
export interface RequestGetCount {
  loginSeq: number;
  type: 0;
}
export interface ResponeseGetCount extends DefaultRespense {
  count: number;
}
