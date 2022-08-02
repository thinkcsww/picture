import React, { FC } from "react";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useNavigation } from "@react-navigation/native";

type AppHeaderProps = {
  title?: string,
  iconName?: string,
  rightButton?: string,
  rightButtonCallback?: () => void
}

const AppHeader: FC<AppHeaderProps> = ({ title, iconName, rightButton, rightButtonCallback }) => {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <MaterialCommunityIcons name={iconName ? iconName : "arrow-left"} color={"black"} size={24} onPress={() => navigation.goBack()} />
      <Text style={styles.title}>{ title ? title : '' }</Text>
      <TouchableOpacity onPress={rightButtonCallback} style={{ width: 28 }}>
        <Text>{ rightButton ? rightButton : '' }</Text>
      </TouchableOpacity>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 20,
    paddingVertical: 10,
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  title: {
    fontWeight: '500',
    fontSize: 16
  }
})

export default AppHeader;
