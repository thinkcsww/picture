import { useCallback, useEffect, useMemo, useState } from "react";
import axios from "axios";
import AsyncStorageService from "../services/AsyncStorageService";
import { Auth } from "../types/Auth";
import { AuthService } from "../services/AuthService";

export const instance = axios.create({
  baseURL: 'http://192.168.200.167:8080/api',
  headers: {
    "Content-Type": "application/json",
  },
});

export const useAxiosLoader = () => {
  const [counter, setCounter] = useState(0);
  const [ready, setReady] = useState(false);
  const inc = useCallback(
    () => {
      setCounter((counter) => counter + 1)
    },
    [setCounter]
  ); // add to counter
  const dec = useCallback(
    () => setCounter((counter) => counter - 1),
    [setCounter]
  ); // remove from counter

  const interceptors = useMemo(
    () => ({
      request: async (config: any) => {
        config.withCredentials = true;

        inc();

        console.log('=== useAxiosLodaer ===');

        const tokenInfo: Auth.MyOAuth2Token = await AsyncStorageService.getObjectData(AsyncStorageService.Keys.TokenInfo);
        console.log(tokenInfo);

        if (!!tokenInfo) {
          let accessToken = tokenInfo.access_token;
          if (tokenInfo.expires_in < new Date().getTime() && !config.url.includes('/token/refresh')) {

            try {
              const refreshTokenResponse = await AuthService.refreshToken(tokenInfo.refresh_token);
              console.log('==== useAxiosLoader Refresh Token ====');
              console.log(refreshTokenResponse);
              accessToken = refreshTokenResponse.access_token;

              await AuthService.setTokenInfo(refreshTokenResponse);
            } catch (e: any) {
              console.log('==== useAxiosLoader Refresh Token Error ====');
              console.log(e.message);
            }

          }

          config.headers.Authorization = `Bearer ${accessToken}`;
        }

        console.log(config);

        return config;
      },
      response: (response: any) => {
        console.log(response);
        if (response.config.headers.Loading === undefined) {
          dec();
        }

        // if (response.data && response.data.code === CommonConstant.RESULT_CODE.NOT_AUTHORIZED) {
        //   console.log(response);
        //   console.log('=== Menu Auth 튕김 ===');
        //   window.location.href = '/console/error';
        //   return;
        // }

        // if(response.data.code == null || response.data.code == undefined) {
        //   window.location.href = '/console/error';
        //   return;
        // }


        return response;
      },
      error: (error: any) => {
        dec();

        console.log(error);
        if (!error.response) {
          console.log(error.response);
          // return Promise.reject(error);
          // window.location.href = process.env.REACT_APP_LOGIN_URL!;
          // window.location.href = '/console/error';
        }

        switch (error.response.status) {
          case 401:
            // window.location.href = '/console/error/unavailable';
            break;
        }

        return Promise.reject(error);
      },
    }),
    []
  ); // create the interceptors

  useEffect(() => {
    // add request interceptors
    const reqInterceptor = instance.interceptors.request.use(
      interceptors.request,
      interceptors.error
    );
    // add response interceptors
    const resInterceptor = instance.interceptors.response.use(
      interceptors.response,
      interceptors.error
    );

    setReady(true);

    return () => {
      // remove all intercepts when done
      instance.interceptors.request.eject(reqInterceptor);
      instance.interceptors.response.eject(resInterceptor);
    };
  }, [interceptors]);

  return [counter > 0, ready];
};
