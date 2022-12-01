import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Colors } from "../../../colors";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";
import { useAppSelector } from "../../../store/config";

type LeftRequestMatchingMessageProps = {
  sendMessage: (body: any) => void,
  roomInfo: Chatting.ChattingRoom,
  roomType: string,
  onClickProfile: () => void,
  message: any
};

const LeftRequestMatchingMessage = ({ message, onClickProfile, roomInfo, roomType, sendMessage }: LeftRequestMatchingMessageProps) => {
  const { user } = useAppSelector(state => state.common);

  const onPressAcceptRequest = () => {
    const body: any = {
      roomId: roomInfo.id,
      senderId: user.id,
      roomType: roomType,
      messageType: Chatting.MessageType.ACCEPT_MATCHING,
      sellerId: roomInfo.opponent.id,
      clientId: user.id,
    }

    sendMessage(body);
  }

  const onPressDeclineRequest = () => {
    const body: any = {
      roomId: roomInfo.id,
      senderId: user.id,
      roomType: roomType,
      messageType: Chatting.MessageType.DECLINE_MATCHING,
      sellerId: roomInfo.opponent.id,
      clientId: user.id,
    }

    sendMessage(body);
  }

  const parsedMessage = JSON.parse(message.message);

  return (
    <View style={{
      flexDirection: 'row',
      marginLeft: 12,
      maxWidth: '70%',
    }}>
      <TouchableOpacity onPress={onClickProfile}>
        <Avatar size={"small"} source={Images.profile.dummy} rounded />
      </TouchableOpacity>

      <View style={{
        backgroundColor: '#d9d9d9',
        paddingHorizontal: 16,
        paddingVertical: 12,
        borderRadius: 12,
        marginLeft: 6,
        flexDirection: 'row'
      }}>
        <View>
          <Text>{`최종 의뢰서가 작성되었습니다.
마감기한: ${DateUtils.getFormattedDate(parsedMessage.dueDate)}
비용: ${parsedMessage.price}원
작업내용: 용왕톤으로 부탁드립니다.

의뢰를 확정하시겠습니까?`}</Text>
          {
            parsedMessage.completeYN === 'Y' && (
              <View style={{
                flexDirection: 'row',
                justifyContent: 'space-between',
                marginTop: 20
              }}>
                <TouchableOpacity style={{
                  backgroundColor: '#b9b0b0',
                  paddingVertical: 12,
                  flex: 1,
                  borderRadius: 8,
                  alignItems: 'center',
                  marginHorizontal: 4
                }} onPress={onPressAcceptRequest}>
                  <Text>예</Text>
                </TouchableOpacity>
                <TouchableOpacity style={{
                  backgroundColor: '#b9b0b0',
                  paddingVertical: 12,
                  flex: 1,
                  borderRadius: 8,
                  alignItems: 'center',
                  marginHorizontal: 4
                }} onPress={onPressDeclineRequest}>
                  <Text>아니오</Text>
                </TouchableOpacity>
              </View>
            )
          }

        </View>

      </View>
      <View style={{
        alignSelf: 'flex-end',
        marginLeft: 4
      }}>
        <Text style={{
          fontSize: 10,
          color: Colors.GRAY_TEXT,
        }}>{DateUtils.getFormattedMessageDate(message.createdDt)}</Text>
      </View>
    </View>
  );
};

export default LeftRequestMatchingMessage;
