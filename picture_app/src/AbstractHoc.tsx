import React, { FC, useEffect } from "react";
import { useAxiosLoader } from "./hooks/useAxiosLoader";
import AsyncStorageService from "./services/AsyncStorageService";
import { useAppDispatch } from "./store/config";
import { setIsTokenExist } from "./store/slices/commonSlice";

const AbstractHoc: FC = ({children}) => {

  const [loading, ready] = useAxiosLoader();
  const dispatch = useAppDispatch();

  useEffect(() => {
    AsyncStorageService.getStringData(AsyncStorageService.Keys.AccessToken).then(result => {
      console.log('AbstractHoc ==== Token')
      console.log(result)
      dispatch(setIsTokenExist(result !== undefined));
    })
  }, [])

  return (
    <>
      {children}
    </>
  )
}

export default AbstractHoc;
