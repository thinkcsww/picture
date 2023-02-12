export namespace User {
  export enum SnsType {
    KAKAO = "KAKAO",
    APPLE = "APPLE"
  }

  export interface VM {
    id: string;
    username: string;
    nickname: string;
    snsType: SnsType;
    description: string;
    workHourFromDt: number;
    workHourToDt: number;
    specialty: string;
    sellerEnabledYn: string;
    createdDt: string;
    updatedDt: string;
    peoplePrice: number;
    backgroundPrice: number;
    officialPrice: number;
    fileName: string;
  }

  export class CreateDto {
    public username?: string;
    public nickname?: string;
    public snsType?: SnsType;
    public description?: string;
    public sellerEnabledYN?: string;
    public workHourFromDt?: string;
    public workHourToDt?: string;
    public specialty?: string;
    public peoplePrice?: string;
    public backgroundPrice?: string;
    public officialPrice?: string;
  }
}
