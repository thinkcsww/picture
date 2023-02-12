import React, { FC } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import RatingStarIcons from "./RatingStarIcons";
import DateUtils from "../../../utils/DateUtils";
import CommonNodata from "../../../components/CommonNodata";
import { useNavigation } from "@react-navigation/native";
import { RouteNames } from "../../../AppNav";
import { Review } from "../../../types/Review";
import ImageWithPH from "../../../components/ImageWithPH";

type SellerDetailReviewProps = {
  review: Review.Review,
  sellerId: string
}

const SellerDetailReview: FC<SellerDetailReviewProps> = ({ review, sellerId }) => {

  const navigation = useNavigation<any>();

  const onPressMore = () => {
    navigation.navigate(RouteNames.SellerReview, { id: sellerId })
  }

  const onPressReport = () => {
  }

  return (
    <View style={styles.container}>
      <Text style={styles.containerTitle}>리뷰</Text>
      {
        review ? (
          <>
            <View style={styles.innerContainer}>
              <ImageWithPH styles={{
                width: 30,
                height: 30,
                borderRadius: 15
              }} fileName={review.writerProfileImageFileName}/>
              {/*<Avatar size={"small"} source={Images.profile.dummy} rounded />*/}
              <View style={styles.profileContainer}>
                <Text style={styles.nicknameText}>{ review.writerNickname }</Text>
                <View style={styles.ratingContainer}>
                  <View style={styles.ratingRow}>
                    <RatingStarIcons rateAvg={review.rate!}/>
                    <Text style={styles.dateText}>{ DateUtils.getPastFormattedDate(review.createdDt) }</Text>
                  </View>

                  <TouchableOpacity>
                    <Text style={styles.reportText}>신고하기</Text>
                  </TouchableOpacity>
                </View>
              </View>
            </View>
            <View style={styles.contentContainer}>

              <Text style={styles.content}>{ review.content }</Text>

              <TouchableOpacity style={styles.moreBtn} onPress={onPressMore}>
                <Text style={styles.moreBtnText}>리뷰 더보기</Text>
              </TouchableOpacity>
            </View>
          </>
        ) : <CommonNodata message={'리뷰가 없습니다'} height={200}/>
      }
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 12,
    marginBottom: 30
  },
  containerTitle: {
    fontWeight: "bold",
    color: "black",
  },
  innerContainer: {
    flexDirection: 'row',
    marginTop: 20,
  },
  profileContainer: {
    marginLeft: 12,
    justifyContent: 'space-between',
    flex: 1
  },
  nicknameText: {
    fontWeight: 'bold'
  },
  ratingContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  ratingRow: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  dateText: {
    marginLeft: 12,
    color: '#b9b9b9',
    fontSize: 12
  },
  reportText: {
    marginLeft: 12,
    color: '#b9b9b9',
    fontSize: 12
  },
  contentContainer: {
    paddingHorizontal: 12
  },
  content: {
    lineHeight: 20,
    marginTop: 20,
  },
  moreBtn: {
    marginTop: 20,
  },
  moreBtnText: {
    color: '#b9b9b9',
    fontSize: 12
  }
})

export default SellerDetailReview;
