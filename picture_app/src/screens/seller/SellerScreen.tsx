import React from "react";
import { Button, SafeAreaView, Text } from "react-native";

const SellerScreen = ({ navigation }) => {
  return <SafeAreaView>
    <Button title={'HI'} onPress={() => navigation.navigate("SellerDetail")}/>
  </SafeAreaView>
}

export default SellerScreen;
