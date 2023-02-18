import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Seller } from "../types/Seller";
import { Specialty } from "../types/Common";

const USER_API_URL = '/v1/users';

export const SellerService = {
  QueryKey: {
    getSellers: 'getSellers',
    getSeller: 'getSeller',
  },

  getSellers: async (selectedFilter: Specialty, filter: Seller.Filter, searchText: string, pageNum: any) => {
    let url = `${USER_API_URL}/seller?page=${pageNum}&size=10&nickname=${searchText}&sort=${filter}`;
    url += `&specialty=${selectedFilter}`
    console.log(url);
    const {data} = await instance.get<PageResult<Seller.Seller>>(url, {
      headers: {
        PermitAll: true
      }
    });

    return data;
  },

  getSeller: async (id: string, requesterId?: string) => {
    let url = `${USER_API_URL}/seller/${id}`;
    console.log(url);
    const {data} = await instance.get<PageResult<Seller.Seller>>(url, {
      headers: {
        PermitAll: true
      },
      params: {
        requesterId
      }
    });

    return data;
  },

}
