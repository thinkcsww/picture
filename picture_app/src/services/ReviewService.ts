import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Review } from "../types/Review";

const REVIEW_API_URL = '/v1/reviews';

export const ReviewService = {
  QueryKey: {
    getReviews: 'getReviews',
  },
  getReviews: async (userId: string, pageNum: any) => {
    let url = `${REVIEW_API_URL}?sellerId=${userId}&page=${pageNum}&size=20`;
    const {data} = await instance.get<PageResult<Review.Review>>(url, {
      headers: {
        PermitAll: true
      }
    });

    return data;
  },
}
