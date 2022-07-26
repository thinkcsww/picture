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
  }

  export enum Filter {
    DEFAULT = '',
    CLOSED = 'CLOSED',
    RATING = 'RATING',
    REVIEW = 'REVIEW',
    PRICE_CHEAP = 'startPrice,asc',
    PRICE_EXPENSIVE = 'startPrice,desc'
  }

  export class CreateDto {
    public username?: string;
    public password?: string;
    public nickname?: string;
    public snsType?: 'KAKAO' | 'APPLE';
    public description?: string;
    public sellerEnabledYn?: string;
    public workHourFromDt?: string;
    public workHourToDt?: string;
    public specialty?: string;
    public peoplePrice?: string;
    public backgroundPrice?: string;
    public officialPrice?: string;
  }
}

