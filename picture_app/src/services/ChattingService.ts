import { Specialty } from "../types/Common";
import { Request } from "../types/Request";
import { instance } from "../hooks/useAxiosLoader";
import { PageResult } from "../types/Page";
import { Chatting } from "../types/Chatting";

const CHATTING_API_URL = '/v1/chattings';

export const ChattingService = {
  QueryKey: {
    enterRoom: 'enterRoom',
    getRooms: 'getRooms',
  },
  enterRoom: async (params: { targetUserId: any; roomId: any }) => {
    let url = `${CHATTING_API_URL}/room/enter`;

    const {data} = await instance.get<PageResult<Request.Request>>(url, {
      params: params,
      headers: {
        PermitAll: false
      }
    });

    return data;
  },
  getRooms: async () => {
    let url = `${CHATTING_API_URL}`;

    const {data} = await instance.get<Chatting.ChattingRoom[]>(url, {
      headers: {
        PermitAll: false
      }
    });

    return data;
  },
  getMessages: async (roomId: string, pagedMessageList: PageResult<Chatting.ChattingMessage>) => {
    let url = `${CHATTING_API_URL}/messages`;

    const {data} = await instance.get<PageResult<Chatting.ChattingMessage>>(url, {
      params: {
        roomId: roomId,
        page: pagedMessageList.number + 1,
        size: pagedMessageList.size,
        sort: 'createdDt,desc'
      },
      headers: {
        PermitAll: false
      }
    });

    return data;
  }
}
