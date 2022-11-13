import { instance } from "../hooks/useAxiosLoader";
import { User } from "../types/User";

const USER_API_URL = '/v1/users'

const UserService = {
  QueryKey: {
    createUser: 'createUser',
    checkNickname: 'checkNickname',
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
    const {data} = await instance.get<User.VM>(url, {
      headers: {
        PermitAll: false
      }
    });

    return data;
  }
}

export default UserService;
