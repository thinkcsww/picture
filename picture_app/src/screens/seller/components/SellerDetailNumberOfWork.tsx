import React from "react";
import { StyleSheet, Text, View } from "react-native";
import { Colors } from "../../../colors";

const SellerDetailNumberOfWork = () => {
  return (
    <View style={styles.container}>

      <Text style={styles.title}>총작업수</Text>

      <View style={styles.numberOfWorkContainer}>
        <View style={styles.numberOfWorkLeftContainer}>
          <Text style={styles.numberOfWorkTotalCountText}>88<Text
            style={styles.numberOfWorkTotalCountInnerText}>건</Text></Text>
        </View>
        <View style={styles.progressRightContainer}>
          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>인물</Text>
            <View style={styles.progressOuterBar}>
              <View style={{ ...styles.progressInnerBar, width: "50%" }}></View>
            </View>
            <Text style={styles.progressCountText}>44</Text>
          </View>

          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>배경</Text>
            <View style={styles.progressOuterBar}>
              <View style={{ ...styles.progressInnerBar, width: "5%" }}></View>
            </View>
            <Text style={styles.progressCountText}>3</Text>
          </View>

          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>증명 </Text>
            <View style={styles.progressOuterBar}>
              <View style={{ ...styles.progressInnerBar, width: "30%" }}></View>
            </View>
            <Text style={styles.progressCountText}>12</Text>
          </View>
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
  numberOfWorkContainer: {
    flexDirection: "row",
    height: 100,
    alignItems: "center",
  },
  numberOfWorkLeftContainer: {
    flex: 4,
    alignItems: "center",
    justifyContent: "center",
  },
  numberOfWorkTotalCountText: {
    fontSize: 40,
    color: "black",
  },
  numberOfWorkTotalCountInnerText: {
    fontSize: 25,
    color: "black",
  },
  progressRightContainer: {
    flex: 8,
  },
  progressRightInnerContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 12,
  },
  progressTypeText: {
    fontWeight: "bold",
    fontSize: 12,
    color: "black",
    marginRight: 20,
  },
  progressOuterBar: {
    width: "60%",
    backgroundColor: "#dcdbdb",
    borderRadius: 12,
    height: 5,
    marginRight: 6,
  },
  progressInnerBar: {
    backgroundColor: Colors.PRIMARY,
    borderRadius: 12,
    height: 5,
  },
  progressCountText: {
    color: Colors.GRAY_TEXT,
    fontSize: 10,
  },
});

export default SellerDetailNumberOfWork;
