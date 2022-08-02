import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Request } from "../types/Request";
import { Specialty } from "../types/Common";

const REQUEST_API_URL = '/v1/requests';

export const RequestService = {
  QueryKey: {
    getRequests: 'getRequests',
    createRequest: 'createRequest'
  },

  getRequests: async (specialty: Specialty, filter: Request.Filter) => {
    let url = `${REQUEST_API_URL}`;
    url += `?requestType=${specialty}&sort=${filter}`
    const {data} = await instance.get<PageResult<Request.Request>>(url);

    return data;
  },

  createRequest: async (dto: Request.CreateDto) => {
    let url = `${REQUEST_API_URL}`;

    const {data} = await instance.post<Request.Request>(url, dto);

    return data;

  }

}
