import React, { FC } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import { Review } from "../../types/Review";
import DateUtils from "../../utils/DateUtils";
import RatingStarIcons from "./components/RatingStarIcons";
import Images from "../../../assets/images";
import ImageWithPH from "../../components/ImageWithPH";

type SellerReviewListItemProps = {
  item: Review.Review
}

const SellerReviewListItem: FC<SellerReviewListItemProps> = ({ item }) => {

  return (
    <View style={styles.container}>
      <View style={styles.innerContainer}>
        <ImageWithPH styles={{
          width: 30,
          height: 30,
          borderRadius: 15
        }} fileName={item.writerProfileImageFileName}/>
        <View style={styles.profileContainer}>
          <Text style={styles.nicknameText}>{item.writerNickname}</Text>
          <View style={styles.ratingContainer}>
            <View style={styles.ratingRow}>
              <RatingStarIcons rateAvg={item.rate!} />
              <Text style={styles.dateText}>{DateUtils.getPastFormattedDate(item.createdDt)}</Text>
            </View>

            <TouchableOpacity>
              <Text style={styles.reportText}>신고하기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
      <View style={styles.contentContainer}>
        <Text style={styles.content}>{ item.content }</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 12,
    marginBottom: 30,
  },
  innerContainer: {
    flexDirection: "row",
    marginTop: 20,
  },
  profileContainer: {
    marginLeft: 12,
    justifyContent: "space-between",
    flex: 1,
  },
  nicknameText: {
    fontWeight: "bold",
  },
  ratingContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  ratingRow: {
    flexDirection: "row",
    alignItems: "center",
  },
  dateText: {
    marginLeft: 12,
    color: "#b9b9b9",
    fontSize: 12,
  },
  reportText: {
    marginLeft: 12,
    color: "#b9b9b9",
    fontSize: 12,
  },
  contentContainer: {
    paddingHorizontal: 12,
  },
  content: {
    lineHeight: 20,
    marginTop: 20,
  },
});

export default SellerReviewListItem;
