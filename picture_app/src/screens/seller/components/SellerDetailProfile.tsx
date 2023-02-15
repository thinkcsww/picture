import React, { FC } from "react";
import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import Images from "../../../../assets/images";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../../colors";
import { Seller } from "../../../types/Seller";
import RatingStarIcons from "./RatingStarIcons";
import { Env } from "../../../constants/Env";
import ImageWithPH from "../../../components/ImageWithPH";

type SellerDetailProfileProps = {
  seller: Seller.Seller,
  onClickChatting: () => void,
  onClickFavorite: () => void
}
const SellerDetailProfile: FC<SellerDetailProfileProps> = ({ seller, onClickChatting, onClickFavorite }) => {
  return (
    <View>
      <View style={styles.container}>
        <ImageWithPH fileName={seller.fileName} styles={styles.profileImage}/>
        <View style={styles.innerContainer}>
          <Text style={styles.name}>{ seller.nickname } 작가님 </Text>
          <View style={styles.rateRow}>
            <RatingStarIcons rateAvg={seller.rateAvg}/>
            <Text style={styles.rate}>{ seller.rateAvg ? seller.rateAvg.toFixed(1) : '0.0'}</Text>
          </View>

          <View style={styles.numberOfWorkContainer}>
            <Text style={styles.numberOfWorkText}>리뷰: { seller.reviewCnt }  I  총 작업 수: { seller.completeMatchingCnt }건</Text>
          </View>

          <View style={styles.buttonContainer}>
            <TouchableOpacity style={styles.button} onPress={onClickChatting}>
              <Icon name={"chatbubbles-outline"} size={20} color={Colors.GRAY_TEXT}/>
              <Text style={styles.buttonTitle}>채팅하기</Text>
            </TouchableOpacity>

            <View style={styles.buttonDivider}/>

            <TouchableOpacity style={styles.button} onPress={onClickFavorite}>
              <Icon name={seller.favorite ? "heart" : "heart-outline"} size={20} color={Colors.GRAY_TEXT}/>
              <Text style={styles.buttonTitle}>단골하기</Text>
            </TouchableOpacity>

          </View>

        </View>
      </View>
      <View style={styles.profileDescContainer}>
        <Text style={styles.profileDesc}>{ seller.description }</Text>
      </View>
    </View>

  );
};

const styles = StyleSheet.create({
  container: {
    marginHorizontal: 12,
    borderRadius: 12,
    padding: 30,
    top: -20,
    backgroundColor: 'white',
    shadowColor: "#000",
    shadowOffset: {
      width: 0,
      height: 4,
    },
    shadowOpacity: 0.32,
    shadowRadius: 5.46,
    elevation: 9,
  },
  innerContainer: {
    alignItems: 'center',
  },
  profileImage: {
    width: 60,
    height: 60,
    borderRadius: 30,
    position: 'absolute',
    top: 20,
    left: 16
  },
  star: {
    paddingHorizontal: 2,
  },
  rateRow: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: 'center',
    width: 200,
    height: 30
  },
  name: {
    color: "black",
    fontSize: 24,
    fontWeight: '500',
    marginLeft: 12
  },
  rate: {
    color: "black",
    marginLeft: 4,
    fontSize: 20
  },
  numberOfWorkContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center'
  },
  numberOfWorkText: {
    fontSize: 13,
    color: Colors.GRAY_TEXT,
    marginTop: 6,
  },
  buttonContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 12
  },
  button: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  buttonTitle: {
    fontSize: 16,
    color: Colors.GRAY_TEXT,
    marginLeft: 8,
  },
  buttonDivider: {
    width:1,
    backgroundColor: Colors.GRAY_TEXT,
    height: 16,
    marginHorizontal: 10
  },
  profileDescContainer: {
    padding: 12,
    borderRadius: 8,
  },
  profileDesc: {
    lineHeight: 20,
    color: "black",
  },
});

export default SellerDetailProfile;
