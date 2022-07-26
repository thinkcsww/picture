import React from "react";
import { Dimensions, StyleSheet, Text, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { Colors } from "../colors";

const CommonNodata = () => {
  return (
    <View style={styles.container}>
      <MaterialCommunityIcons name={'emoticon-sad-outline'} size={70} color={Colors.GRAY_TEXT}/>
      <Text style={styles.text}>결과가 없습니다</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    height: Dimensions.get("window").height / 2,
    justifyContent: 'center',
    alignItems: 'center',
  },
  text: {
    color: Colors.GRAY_TEXT,
    fontWeight: 'bold',
    fontSize: 14,
    marginTop: 8
  }
})

export default CommonNodata;
