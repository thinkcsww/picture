import React, { FC } from "react";
import { StyleSheet, Text, View } from "react-native";

type TabListHeaderTitleProps = {
  title: string
}

const TabListHeaderTitle: FC<TabListHeaderTitleProps> = ({ title }) => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>{ title }</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    height: 70
  },
  title: {
    fontSize: 26,
    color: "black",
  }
})

export default TabListHeaderTitle
