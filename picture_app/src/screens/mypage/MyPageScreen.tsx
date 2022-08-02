import React from "react";
import { Button, SafeAreaView, Text } from "react-native";
import AsyncStorageService from "../../services/AsyncStorageService";

const MyPageScreen = () => {

  const logout = async () => {
    await AsyncStorageService.removeData(AsyncStorageService.Keys.AccessToken);
    await AsyncStorageService.removeData(AsyncStorageService.Keys.RefreshToken);
  }
  return <SafeAreaView>
    <Text>my page</Text>
    <Button title={'로그아웃'} onPress={logout}/>
  </SafeAreaView>
}

export default MyPageScreen;
