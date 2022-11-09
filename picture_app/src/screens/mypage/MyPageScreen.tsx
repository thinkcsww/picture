import React, { useEffect } from "react";
import { Button, SafeAreaView, Text } from "react-native";
import AsyncStorageService from "../../services/AsyncStorageService";
import UserService from "../../services/UserService";

const MyPageScreen = () => {

  useEffect(() => {
    UserService.getUserMe().then();
  }, [])

  const logout = async () => {
    await AsyncStorageService.removeData(AsyncStorageService.Keys.TokenInfo);
  }
  return <SafeAreaView>
    <Text>my page</Text>
    <Button title={'로그아웃'} onPress={logout}/>
  </SafeAreaView>
}

export default MyPageScreen;
