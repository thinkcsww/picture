import { Specialty } from "./Common";

export namespace Request {
  export interface Request {
    id: string,
    userId: string,
    userNickname: string,
    userAcceptRate: number,
    requestType: Specialty,
    title: string,
    desiredPrice: number,
    dueDate: string,
    description: string,
    matchYn: string,
    readCount: number,
    chatCount: number,
    anotherRequests: Request.Request[],
  }

  export enum Filter {
    DEFAULT = '',
    PRICE = 'desiredPrice,asc',
    DUE_DATE = 'dueDate,asc',
  }
}
