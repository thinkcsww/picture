import React, { useEffect } from "react";
import { Button, SafeAreaView, Text } from "react-native";
import AsyncStorageService from "../../services/AsyncStorageService";
import UserService from "../../services/UserService";
import { useAppDispatch } from "../../store/config";
import { setUser } from "../../store/slices/commonSlice";

const MyPageScreen = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    UserService.getUserMe().then((res: any) => {
      dispatch(setUser(res.data))
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
