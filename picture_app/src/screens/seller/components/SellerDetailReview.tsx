import React from "react";
import { StyleSheet, Text, View } from "react-native";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../../colors";

const SellerDetailReview = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.infoTitle}>리뷰</Text>
      <View style={styles.rateContainer}>
        <View style={styles.rateLeftContainer}>
          <Text style={styles.rateTotalCountText}>4.3</Text>
          <View style={{
            flexDirection: "row",
          }}>
            <Icon name={"ios-star"} size={14} color={Colors.PRIMARY} style={styles.rateStar}/>
            <Icon name={"ios-star"} size={14} color={Colors.PRIMARY} style={styles.rateStar}/>
            <Icon name={"ios-star"} size={14} color={Colors.PRIMARY} style={styles.rateStar}/>
            <Icon name={"ios-star"} size={14} color={Colors.PRIMARY} style={styles.rateStar}/>
            <Icon name={"ios-star"} size={14} color={Colors.PRIMARY} style={styles.rateStar}/>
          </View>

        </View>
        <View style={styles.progressRightContainer}>
          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>5점 </Text>
            <View style={styles.progressOuterBar}>
              <View style={{ ...styles.progressInnerBar, width: "30%" }}></View>
            </View>
            <Text style={styles.progressCountText}>12</Text>
          </View>
          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>4점 </Text>
            <View style={styles.progressOuterBar}>
              <View style={{ ...styles.progressInnerBar, width: "30%" }}></View>
            </View>
            <Text style={styles.progressCountText}>12</Text>
          </View>
          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>3점 </Text>
            <View style={styles.progressOuterBar}>
              <View style={{ ...styles.progressInnerBar, width: "30%" }}></View>
            </View>
            <Text style={styles.progressCountText}>12</Text>
          </View>
          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>2점 </Text>
            <View style={styles.progressOuterBar}>
              <View style={{ ...styles.progressInnerBar, width: "30%" }}></View>
            </View>
            <Text style={styles.progressCountText}>12</Text>
          </View>
          <View style={styles.progressRightInnerContainer}>
            <Text style={styles.progressTypeText}>1점 </Text>
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
    infoTitle: {
    fontWeight: "bold",
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
  rateContainer: {
    flexDirection: "row",
    alignItems: "center",
  },
  rateLeftContainer: {
    flex: 4,
    alignItems: "center",
    justifyContent: "center",
  },
  rateTotalCountText: {
    fontSize: 40,
    color: "black",
  },
  rateStar: {
    paddingLeft: 2
  }
});

export default SellerDetailReview;

