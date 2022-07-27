import { instance } from "../hooks/useAxiosLoader";
import { Auth } from "../types/Auth";

const AUTH_API_URL = '/v1/auth';

export const AuthService = {
  QueryKey: {
    login: 'login',
    signUp: 'signUp',
  },

  login: async (dto: Auth.LoginDto) => {
    let url = `${AUTH_API_URL}/login`;
    const {data} = await instance.post<Auth.MyOAuth2Token>(url, dto);

    return data;
  },

}
