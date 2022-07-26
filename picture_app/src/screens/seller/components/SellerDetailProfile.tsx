import React, { FC } from "react";
import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import Images from "../../../../assets/images";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../../colors";

const SellerDetailProfile: FC = () => {
  return (
    <View>
      <View style={styles.container}>
        <Image source={{ uri: "" }} defaultSource={Images.profile.dummy} style={styles.profileImage} />
        <View style={styles.innerContainer}>
          <Text style={styles.name}>이병묵 작가님</Text>
          <View style={styles.rateRow}>
            <Icon name={"ios-star"} size={18} color={Colors.PRIMARY} style={styles.star}/>
            <Icon name={"ios-star"} size={18} color={Colors.PRIMARY} style={styles.star}/>
            <Icon name={"ios-star"} size={18} color={Colors.PRIMARY} style={styles.star}/>
            <Icon name={"ios-star"} size={18} color={Colors.PRIMARY} style={styles.star}/>
            <Icon name={"ios-star"} size={18} color={Colors.PRIMARY} style={styles.star}/>
            <Text style={styles.rate}>4.4</Text>
          </View>

          <View style={styles.numberOfWorkContainer}>
            <Text style={styles.numberOfWorkText}>리뷰: 90  I  총 작업 수: 88건</Text>
          </View>

          <View style={styles.buttonContainer}>
            <TouchableOpacity style={styles.button}>
              <Icon name={"chatbubbles-outline"} size={20} color={Colors.GRAY_TEXT}/>
              <Text style={styles.buttonTitle}>채팅하기</Text>
            </TouchableOpacity>

            <View style={styles.buttonDivider}/>

            <TouchableOpacity style={styles.button}>
              <Icon name={"heart-outline"} size={20} color={Colors.GRAY_TEXT}/>
              <Text style={styles.buttonTitle}>단골하기</Text>
            </TouchableOpacity>

          </View>

        </View>
      </View>
      <View style={styles.profileDescContainer}>
        <Text style={styles.profileDesc}>
          대전에 종로구에서 사진관을 운영하고있는 사진작가 이병묵입니다. 다년간의 노하우a로 어쩌고저쪼고 예쁘게 뿅뿅 할게요~~뿌잉
          인물작업 위주로 하고있구요 배경은 간단한 작업 선에서는 가능합니다!

        </Text>
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
    marginRight: 12,
    fontSize: 25,
    fontWeight: '500',
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
