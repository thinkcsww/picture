import React, { useEffect } from "react";
import { SafeAreaView, Text } from "react-native";
import { useAppSelector } from "../../store/config";
import { useNavigation } from "@react-navigation/native";

const ChattingScreen = () => {

  const navigation = useNavigation();

  return <SafeAreaView>
    <Text>chatting</Text>
  </SafeAreaView>
}

export default ChattingScreen;
