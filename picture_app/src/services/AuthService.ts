import { instance } from "../hooks/useAxiosLoader";
import { Auth } from "../types/Auth";
import AsyncStorageService from "./AsyncStorageService";

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
  refreshToken: async (refreshToken: string) => {
    let url = `${AUTH_API_URL}/token/refresh`;
    const {data} = await instance.post<Auth.MyOAuth2Token>(url, {
      refreshToken: refreshToken
    });

    return data;
  },
  setTokenInfo: async (tokenInfo: Auth.MyOAuth2Token) => {
    tokenInfo.expires_in = new Date().getTime() + tokenInfo.expires_in;

    await AsyncStorageService.setObjectData(AsyncStorageService.Keys.TokenInfo, tokenInfo);
  },



}
