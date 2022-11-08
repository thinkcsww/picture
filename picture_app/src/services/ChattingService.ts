import { Specialty } from "../types/Common";
import { Request } from "../types/Request";
import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";

const CHATTING_API_URL = '/v1/chattings';

export const ChattingService = {
  QueryKey: {
    getRoom: 'getRoom',
  },
  getRoom: async (targetUserId: string) => {
    let url = `${CHATTING_API_URL}/room/user/${targetUserId}`;

    const {data} = await instance.get<PageResult<Request.Request>>(url, {
      headers: {
        PermitAll: false
      }
    });

    return data;
  },
}
