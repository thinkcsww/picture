import React, { FC, useEffect } from "react";
import { useAxiosLoader } from "./hooks/useAxiosLoader";
import AsyncStorageService from "./services/AsyncStorageService";
import { useAppDispatch } from "./store/config";
import { setIsTokenExist, setUser } from "./store/slices/commonSlice";
import { Auth } from "./types/Auth";
import { User } from "./types/User";
import axios, { AxiosError } from "axios";
import { HttpStatus } from "./constants/HttpStatus";
import { Env } from "./constants/Env";

const AbstractHoc: FC = ({children}) => {

  const [loading, ready] = useAxiosLoader();
  const dispatch = useAppDispatch();

  useEffect(() => {
    getUserMe();
  }, [])

  const getUserMe = () => {
    AsyncStorageService.getObjectData(AsyncStorageService.Keys.TokenInfo).then((token: Auth.MyOAuth2Token) => {
      if (token) {
        let url = `${Env.host}/api/v1/users/me`;
        axios.get<User.VM>(url, {
          headers: {
            Authorization: `Bearer ${token.access_token}`
          }
        })
          .then((res) => {

            if (res.status === HttpStatus.OK) {
              console.log('=== AbstractHoc user me 성공 ===')
              console.log(res.data);
              dispatch(setIsTokenExist(true));
            } else {
              console.log('=== AbstractHoc user me 실패 ===')
            }

            console.log(res);
            dispatch(setUser(res.data))
          })
          .catch((e: AxiosError) => {
            console.log('=== AbstractHoc user me 에러 ===')
            console.log(e);

            if (e && e.response && e.response.status === HttpStatus.UNAUTHORIZED) {
              dispatch(setIsTokenExist(false));
              AsyncStorageService.removeData(AsyncStorageService.Keys.TokenInfo).then();
            }
          })
      }
    })
  }


  if (!ready) {
    console.log('not ready');
    return null;
  }

  return (
    <>
      {children}
    </>
  )
}

export default AbstractHoc;
