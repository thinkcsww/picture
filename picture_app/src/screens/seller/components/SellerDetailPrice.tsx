import React from "react";
import { StyleSheet, Text, View } from "react-native";

const SellerDetailPrice = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>비용</Text>
      <View style={styles.innerContainer}>
        <Text>인물: 2000원~ </Text>
        <View style={styles.workTime}>
          <Text style={styles.workTimeText}>평균 작업시간: 35분</Text>
        </View>
      </View>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 12,
  },
  title: {
    fontWeight: "bold",
    color: "black",
  },
  innerContainer: {
    flexDirection: "row",
    height: 20,
    alignItems: "center",
    marginTop: 5,
  },
  workTime: {
    backgroundColor: "#0000000F",
    padding: 2,
    borderRadius: 8,
  },
  workTimeText: {
    color: 'black'
  }
})

export default SellerDetailPrice;
