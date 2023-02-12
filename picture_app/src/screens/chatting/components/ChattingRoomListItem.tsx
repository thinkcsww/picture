import { useNavigation } from "@react-navigation/native";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Badge } from "@rneui/base";
import React from "react";
import { RouteNames } from "../../../AppNav";
import { Chatting } from "../../../types/Chatting";
import { Colors } from "../../../colors";
import CommonUtils from "../../../utils/CommonUtils";
import DateUtils from "../../../utils/DateUtils";
import ImageWithPH from "../../../components/ImageWithPH";

type ChattingRoomListItemProps = {
  item: Chatting.ChattingRoom
}

const ChattingRoomListItem = ({ item }: ChattingRoomListItemProps) => {
  const navigation = useNavigation<any>();

  const onPress = () => {
    navigation.navigate(RouteNames.ChattingRoom, { roomId: item.id });
  }

  const getMessage = () => {
    if (item.lastMessage.messageType === Chatting.MessageType.REQUEST_MATCHING) {
      return '매칭 의뢰서'
    } else if (item.lastMessage.messageType === Chatting.MessageType.ACCEPT_MATCHING) {
      return '매칭이 수락되었습니다.'
    } else if (item.lastMessage.messageType === Chatting.MessageType.DECLINE_MATCHING) {
      return '매칭이 거절되었습니다.'
    } else if (item.lastMessage.messageType === Chatting.MessageType.MESSAGE) {
      return item.lastMessage.message;
    }
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
        <ImageWithPH styles={{
          width: 50,
          height: 50,
          borderRadius: 25
        }} fileName={item.opponent.fileName}/>
        <View style={{
          marginLeft: 12
        }}>
          <Text style={{
            fontWeight: 'bold',
            fontSize: 17
          }}>{ item.opponent.nickname }</Text>
          <Text numberOfLines={2}
                style={{
                  color: '#575757',
                  fontSize: 13,
                  marginTop: 4
                }}>{ getMessage()  }</Text>
        </View>
      </View>
      <View style={{
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'flex-end',
        padding: 8,
        borderRadius: 8
      }}>
        <Text style={{
          color: Colors.GRAY_TEXT,
          fontSize: 12
        }}>{DateUtils.getFormattedMessageDate(item.lastMessage.createdDt)}</Text>
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
