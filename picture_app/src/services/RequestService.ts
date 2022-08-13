import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Request } from "../types/Request";
import { Specialty } from "../types/Common";

const REQUEST_API_URL = '/v1/requests';

export const RequestService = {
  QueryKey: {
    getRequests: 'getRequests',
    getRequest: 'getRequest',
    createRequest: 'createRequest'
  },

  getRequests: async (specialty: Specialty, filter: Request.Filter, pageNum: number) => {
    let url = `${REQUEST_API_URL}`;
    url += `?specialty=${specialty}&page=${pageNum}&sort=${filter}&size=10`

    console.log(url);
    const {data} = await instance.get<PageResult<Request.Request>>(url, {
      headers: {
        PermitAll: true
      }
    });

    return data;
  },

  createRequest: async (dto: Request.CreateDto) => {
    let url = `${REQUEST_API_URL}`;

    const { data } = await instance.post<Request.Request>(url, dto, {
      headers: {
        "PermitAll": false
      }
    });

    return data;

  },

  getRequest: async (id: string) => {
    let url = `${REQUEST_API_URL}/${id}`;

    const {data} = await instance.get<Request.Request>(url);

    return data;

  }

}
