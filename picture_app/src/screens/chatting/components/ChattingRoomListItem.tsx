import { useNavigation } from "@react-navigation/native";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Badge } from "@rneui/base";
import React from "react";
import { RouteNames } from "../../../AppNav";

type ChattingRoomListItemProps = {
  item: any
}

const ChattingRoomListItem = ({ item }: ChattingRoomListItemProps) => {
  const navigation = useNavigation<any>();

  const onPress = () => {
    navigation.navigate(RouteNames.ChattingRoom);
  }

  return (
    <TouchableOpacity style={{
      flexDirection: 'row',
      marginHorizontal: 8,
      marginVertical: 6,
      backgroundColor: '#e6e6e6',
      paddingHorizontal: 8,
      paddingVertical: 16,
      borderRadius: 8,
      alignItems: 'center',
      justifyContent: 'space-between'
    }} onPress={onPress}>
      <View style={{
        flexDirection: 'row',
        width: '45%',
      }}>
        <Avatar size={"medium"} source={Images.profile.dummy} rounded />
        <View style={{
          marginLeft: 12
        }}>
          <Text style={{
            fontWeight: 'bold',
            fontSize: 17
          }}>이병묵 작가님</Text>
          <Text numberOfLines={2}
                style={{
                  color: '#575757',
                  fontSize: 13,
                  marginTop: 4
                }}>네~ ASAP 부탁해요~ 네~네~ ASAP 부탁해요~ 네~네~ ASAP 부탁해요~ 네~네~ ASAP 부탁해요~ 네~네~ ASAP 부탁해요~ 네~</Text>
        </View>
      </View>
      <View style={{
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'flex-end',
        backgroundColor: '#c9c9c9',
        padding: 8,
        borderRadius: 8
      }}>
        <View style={{
          alignItems: 'center',
          marginRight: 8,
          marginLeft: 8
        }}>
          <Text style={{
            fontSize: 16,
            fontWeight: '500',
            marginBottom: 4
          }}>작업중</Text>
          <Text style={{
            color: '#595959',
            fontSize: 10
          }}>마감: 6일전</Text>
        </View>
        <Avatar avatarStyle={{ borderRadius: 8 }} size={50} source={Images.profile.dummy} />
      </View>
      <Badge value={10} status={'error'} containerStyle={{
        position: 'absolute',
        top: -5,
        left: -5
      }} />
    </TouchableOpacity>
  )
}

export default ChattingRoomListItem;
