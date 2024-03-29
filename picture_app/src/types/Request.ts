import { Specialty } from "./Common";

export namespace Request {
  export interface Request {
    id: string,
    userId: string,
    userNickname: string,
    userAcceptRate: number,
    specialty: Specialty,
    title: string,
    desiredPrice: number,
    dueDate: string,
    description: string,
    matchYn: string,
    readCount: number,
    chatCount: number,
    userProfileFileName: string,
    anotherRequests: Request.Request[],
  }

  export enum Filter {
    DEFAULT = '',
    PRICE = 'desiredPrice,asc',
    DUE_DATE = 'dueDate,asc',
  }

  export class CreateDto {
    public specialty?: Specialty;
    public title?: string;
    public desiredPrice?: number;
    public dueDate?: string;
    public description?: string;
    public matchYn?: string;
    public completeYn?: string;
  }
}
