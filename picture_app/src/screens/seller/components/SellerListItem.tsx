import React, { FC } from "react";
import { Seller } from "../../../types/Seller";
import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import Images from "../../../../assets/images";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../../colors";
import { useNavigation } from "@react-navigation/native";
import { RouteNames } from "../../../AppNav";

type SellerListItemProps = {
  item: Seller.Seller
}
const SellerListItem: FC<SellerListItemProps> = ({ item }) => {
  console.log(item);
  const navigation = useNavigation<any>();

  const onPress = () => {
    navigation.navigate(RouteNames.SellerDetail, { id: item.id })
  }
  return (
    <TouchableOpacity onPress={onPress} style={styles.container}>
      <Image
        source={{ uri: 'asd' }}
        defaultSource={Images.profile.dummy}
        style={styles.profileImage}
      />

      <View>
        <View style={styles.infoContainer}>
          <Text style={styles.name}>{item.nickname} 작가님</Text>
        </View>
        <View style={styles.infoContainer}>
          <Text
            numberOfLines={1}
            style={styles.desc}>{item.description ? item.description : '반갑습니다.'}</Text>
        </View>

        <View style={styles.additionalInfoContainer}>
          <View style={{
            flexDirection: "row",
          }}>
            <Icon name={"ios-star"} size={14} color={Colors.PRIMARY} />
            <Text style={styles.infoDesc}> 4.8</Text>
          </View>

          <View style={{
            flexDirection: "row",
            alignItems: 'center'
          }}>
            <Text style={styles.infoTitle}>채택률</Text>
            <Text style={styles.infoDesc}> 95%</Text>
          </View>

          <View style={{
            flexDirection: "row",
          }}>
            <Text style={styles.infoTitle}>리뷰</Text>
            <Text style={styles.infoDesc}>(13)</Text>
          </View>
        </View>

        <View style={styles.infoContainer}>
          <Text style={styles.infoDesc}>2000원~ (평균 2800원)</Text>
        </View>

        <View style={{
          flexDirection: 'row',
          marginTop: 8
        }}>
          <View style={{
            backgroundColor: '#C4C4C4',
            marginRight: 2,
            borderRadius: 10,
            padding: 4,
            flexWrap: 'wrap'
          }}>
            <Text style={{
              fontSize: 10
            }}>실시간 참여가능</Text>
          </View>
          <View style={{
            backgroundColor: '#C4C4C4',
            marginRight: 2,
            borderRadius: 10,
            padding: 4,
            flexWrap: 'wrap'
          }}>
            <Text style={{
              fontSize: 10
            }}>예약 가능</Text>
          </View>
        </View>
      </View>

    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 20,
    flexDirection: "row",
    height: 100,
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
    width: '80%'
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
