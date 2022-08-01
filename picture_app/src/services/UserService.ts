import { instance } from "../hooks/useAxiosLoader";
import { Auth } from "../types/Auth";
import { User } from "../types/User";

const USER_API_URL = '/v1/users'

const UserService = {
  QueryKey: {
    createUser: 'createUser',
    checkNickname: 'checkNickname',
  },
  checkNickname: async (nickname: string) => {
    let url = `${USER_API_URL}/check-nickname?nickname=${nickname}`;
    return await instance.get<any>(url);
  },
  signUp: async (dto: User.CreateDto) => {
    let url = `${USER_API_URL}`;
    const {data} = await instance.post<User.VM>(url, dto);

    return data;
  }
}

export default UserService;
