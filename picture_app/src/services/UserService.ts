import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Seller } from "../types/Seller";
import { Specialty } from "../types/Common";

const USER_API_URL = '/v1/users';

export const UserService = {
  QueryKey: {
    getSellers: 'getSellers'
  },

  getSellers: async (selectedFilter: Specialty, value: Seller.Filter) => {
    let url = `${USER_API_URL}/seller`;
    url += `?specialty=${selectedFilter}`
    const {data} = await instance.get<PageResult<Seller.Seller>>(url);

    return data;
  },

}
