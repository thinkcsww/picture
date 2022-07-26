import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Request } from "../types/Request";
import { Specialty } from "../types/Common";

const REQUEST_API_URL = '/v1/requests';

export const RequestService = {
  QueryKey: {
    getRequests: 'getRequests'
  },

  getRequests: async (specialty: Specialty, filter: Request.Filter) => {
    let url = `${REQUEST_API_URL}`;
    url += `?requestType=${specialty}&sort=${filter}`
    const {data} = await instance.get<PageResult<Request.Request>>(url);

    return data;
  },

}
