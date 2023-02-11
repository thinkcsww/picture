import React, { FC } from "react";
import { Seller } from "../../../types/Seller";
import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import Images from "../../../../assets/images";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../../colors";
import { useNavigation } from "@react-navigation/native";
import { RouteNames } from "../../../AppNav";
import { Env } from "../../../constants/Env";
import ImageWithPH from "../../../components/ImageWithPH";

type SellerListItemProps = {
  item: Seller.Seller
}
const SellerListItem: FC<SellerListItemProps> = ({ item }) => {
  const navigation = useNavigation<any>();

  const onPress = () => {
    navigation.navigate(RouteNames.SellerDetail, { id: item.id })
  }
  return (
    <TouchableOpacity onPress={onPress} style={styles.container}>
      <ImageWithPH fileName={item.fileName} styles={styles.profileImage}/>
      <View>
        <View style={styles.infoContainer}>
          <Text style={styles.name}>{item.nickname} 작가님</Text>
        </View>
        <View style={{ width: '88%' }}>
          <Text numberOfLines={2} style={styles.desc}>{item.description}</Text>
        </View>

        <View style={styles.additionalInfoContainer}>
          <View style={{
            flexDirection: "row",
          }}>
            <Icon name={"ios-star"} size={14} color={Colors.PRIMARY} />
            <Text style={styles.infoDesc}>{item.rateAvg ? item.rateAvg.toFixed(1) : '0.0'}</Text>
          </View>

          <View style={{
            flexDirection: "row",
            alignItems: 'center'
          }}>
            <Text style={styles.infoTitle}>작업건수</Text>
            <Text style={styles.infoDesc}> {item.completeMatchingCnt}건</Text>
          </View>

          <View style={{
            flexDirection: "row",
          }}>
            <Text style={styles.infoTitle}>리뷰</Text>
            <Text style={styles.infoDesc}>({item.reviewCnt})</Text>
          </View>
        </View>

        <View style={styles.infoContainer}>
          <Text style={styles.infoDesc}>{item.price?.toLocaleString()}원</Text>
        </View>

        {/*<View style={{*/}
        {/*  flexDirection: 'row',*/}
        {/*  marginTop: 8*/}
        {/*}}>*/}
        {/*  <View style={{*/}
        {/*    backgroundColor: '#C4C4C4',*/}
        {/*    marginRight: 2,*/}
        {/*    borderRadius: 10,*/}
        {/*    padding: 4,*/}
        {/*    flexWrap: 'wrap'*/}
        {/*  }}>*/}
        {/*    <Text style={{*/}
        {/*      fontSize: 10*/}
        {/*    }}>실시간 참여가능</Text>*/}
        {/*  </View>*/}
        {/*  <View style={{*/}
        {/*    backgroundColor: '#C4C4C4',*/}
        {/*    marginRight: 2,*/}
        {/*    borderRadius: 10,*/}
        {/*    padding: 4,*/}
        {/*    flexWrap: 'wrap'*/}
        {/*  }}>*/}
        {/*    <Text style={{*/}
        {/*      fontSize: 10*/}
        {/*    }}>예약 가능</Text>*/}
        {/*  </View>*/}
        {/*</View>*/}
      </View>

    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 20,
    flexDirection: "row",
  },
  profileImage: {
    width: 80,
    height: 80,
    borderRadius: 8,
    backgroundColor: "red",
    marginRight: 12,
  },
  name: {
    fontSize: 16,
    fontWeight: '500',
    color: 'black'
  },
  desc: {
    fontSize: 12,
    flexShrink: 1
  },
  infoContainer: {
    marginTop: 2,
  },
  infoTitle: {
    fontSize: 13,
    fontWeight: '500',
  },
  infoDesc: {
    fontSize: 12,
  },
  additionalInfoContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    width: 200,
    marginTop: 4
  }
})

export default SellerListItem;
