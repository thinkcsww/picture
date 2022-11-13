import React, { useEffect, useState } from "react";
import { Button, SafeAreaView, Text } from "react-native";
import AsyncStorageService from "../../services/AsyncStorageService";
import UserService from "../../services/UserService";
import { useAppDispatch, useAppSelector } from "../../store/config";
import { setUser } from "../../store/slices/commonSlice";

const MyPageScreen = () => {
  const dispatch = useAppDispatch();

  React.useEffect(() => {
    UserService.getUserMe().then((res: any) => {
      console.log(res);
      dispatch(setUser(res))
    });
  }, [])



  const logout = async () => {
    await AsyncStorageService.removeData(AsyncStorageService.Keys.TokenInfo);
    dispatch(setUser(undefined));
  }

  return <SafeAreaView>
    <Text>my page</Text>
    <Button title={'로그아웃'} onPress={logout}/>
  </SafeAreaView>
}

export default MyPageScreen;
