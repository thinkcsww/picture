import React, { FC, useEffect } from "react";
import { useAxiosLoader } from "./hooks/useAxiosLoader";
import AsyncStorageService from "./services/AsyncStorageService";
import { useAppDispatch } from "./store/config";
import { setIsTokenExist } from "./store/slices/commonSlice";
import { Auth } from "./types/Auth";

const AbstractHoc: FC = ({children}) => {

  const [loading, ready] = useAxiosLoader();
  const dispatch = useAppDispatch();

  useEffect(() => {
    AsyncStorageService.getObjectData(AsyncStorageService.Keys.TokenInfo).then((result: Auth.MyOAuth2Token) => {
      console.log('AbstractHoc ==== Token')
      console.log(result)
      dispatch(setIsTokenExist(!!result));
    })
  }, [])

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
