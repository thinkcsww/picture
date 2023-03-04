import { instance } from "../hooks/useAxiosLoader";
import { User } from "../types/User";
import { Result } from "../types/Page";

const USER_API_URL = '/v1/users'

const UserService = {
  QueryKey: {
    createUser: 'createUser',
    checkNickname: 'checkNickname',
    toggleFavorite: 'toggleFavorite'
  },
  checkNickname: async (nickname: string) => {
    let url = `${USER_API_URL}/check-nickname?nickname=${nickname}`;
    return await instance.get<any>(url, {
      headers: {
        PermitAll: true
      }
    });
  },
  signUp: async (dto: User.CreateDto) => {
    let url = `${USER_API_URL}`;
    const {data} = await instance.post<User.VM>(url, dto, {
      headers: {
        PermitAll: true
      }
    });

    return data;
  },
  getUserMe: async () => {
    let url = `${USER_API_URL}/me`;
    const {data} = await instance.get<Result<User.VM>>(url, {
      headers: {
        PermitAll: false
      }
    });

    return data;
  },
  toggleFavorite: async (id: string, targetUserId: string) => {
    let url = `${USER_API_URL}/favorites`;
    const {data} = await instance.post(url, {
      userId: id,
      targetUserId: targetUserId
    });
    return data;
  },
}

export default UserService;
