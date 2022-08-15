import React, { FC } from "react";
import { StyleSheet, Text, View } from "react-native";
import { Seller } from "../../../types/Seller";

type SellerDetailPriceProps = {
  seller: Seller.Seller
}

const SellerDetailPrice: FC<SellerDetailPriceProps> = ({ seller }) => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>비용</Text>
      <View style={styles.innerContainer}>
        <Text>인물사진: { seller.peoplePrice }원~ </Text>
        <View style={styles.workTime}>
          <Text style={styles.workTimeText}>평균 작업시간: 35분</Text>
        </View>
      </View>
      <View style={styles.innerContainer}>
        <Text>배경사진: { seller.backgroundPrice }원~ </Text>
        <View style={styles.workTime}>
          <Text style={styles.workTimeText}>평균 작업시간: 35분</Text>
        </View>
      </View>
      <View style={styles.innerContainer}>
        <Text>증명사진: { seller.officialPrice }원~ </Text>
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
    marginBottom: 8
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
