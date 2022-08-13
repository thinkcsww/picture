import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Seller } from "../types/Seller";
import { Specialty } from "../types/Common";

const USER_API_URL = '/v1/users';

export const SellerService = {
  QueryKey: {
    getSellers: 'getSellers'
  },

  getSellers: async (selectedFilter: Specialty, filter: Seller.Filter, pageNum: any) => {
    let url = `${USER_API_URL}/seller?page=${pageNum}&size=10`;
    url += `?specialty=${selectedFilter}`
    console.log(url);
    const {data} = await instance.get<PageResult<Seller.Seller>>(url, {
      headers: {
        PermitAll: true
      }
    });

    return data;
  },

}
