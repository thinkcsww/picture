import { User } from "./User";

export namespace Seller {
  export interface Seller {
    id: string,
    username: string,
    nickname: string,
    snsType: string,
    description: string,
    workHourFromDt: number,
    workHourToDt: number,
    specialty: string,
    sellerEnabledYn: string,
    createdDt: string,
    updatedDt: string,
    reviewCnt: number,
    rateAvg: number,
    price: number, // 리스트 표시용
    officialPrice: number,
    peoplePrice: number,
    backgroundPrice: number,
    completeMatchingCnt: number
    matchingCountBySpecialty: any[],
  }

  export enum Filter {
    DEFAULT = '',
    MATCHING = 'matching',
    RATING = 'rating',
    REVIEW = 'review',
    PRICE = 'price',
    // PRICE_EXPENSIVE = 'startPrice,desc'
  }

  export class CreateDto {
    public username?: string;
    public password?: string;
    public nickname?: string;
    public snsType?: User.SnsType
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

