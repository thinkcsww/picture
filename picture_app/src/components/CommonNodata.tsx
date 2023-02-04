import React, { FC } from "react";
import { Dimensions, StyleSheet, Text, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { Colors } from "../colors";

type CommonNodataProps = {
  message?: string,
  height?: number
}
const CommonNodata: FC<CommonNodataProps> = ({ message, height }) => {
  return (
    <View style={{ ...styles.container, height: height ? height: Dimensions.get("window").height / 2 }}>
      <MaterialCommunityIcons name={'emoticon-sad-outline'} size={70} color={Colors.GRAY_TEXT}/>
      <Text style={styles.text}>{ message ? message : '결과가 없습니다' }</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
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
