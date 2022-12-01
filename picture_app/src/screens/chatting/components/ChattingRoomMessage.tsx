import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Colors } from "../../../colors";
import { useAppSelector } from "../../../store/config";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";
import LeftTextMessage from "./LeftTextMessage";
import LeftRequestMatchingMessage from "./LeftRequestMatchingMessage";
import RightTextMessage from "./RightTextMessage";
import RightRequestMatchingMessage from "./RightRequestMatchingMessage";
import RightAcceptMatchingMessage from "./RightAcceptMatchingMessage";
import LeftAcceptMatchingMessage from "./LeftAcceptMatchingMessage";

type ChattingRoomMessageListProps = {
  item: Chatting.ChattingMessage,
  sendMessage: (body: any) => void,
  roomInfo: Chatting.ChattingRoom,
  roomType: string
};

const ChattingRoomMessage = ({item, sendMessage, roomInfo, roomType}: ChattingRoomMessageListProps) => {
  const { user } = useAppSelector(state => state.common);

  const onClickProfile = () => {
  }

  if (item.senderId !== user.id) {
    if (item.messageType === Chatting.MessageType.REQUEST_MATCHING) {
      return <LeftRequestMatchingMessage roomInfo={roomInfo} roomType={roomType} sendMessage={sendMessage}  message={item} onClickProfile={onClickProfile}/>
    } else if (item.messageType === Chatting.MessageType.ACCEPT_MATCHING) {
      return <LeftAcceptMatchingMessage message={item} onClickProfile={onClickProfile}/>
    }

    return <LeftTextMessage message={item} onClickProfile={onClickProfile}/>

  } else {
    if (item.messageType === Chatting.MessageType.REQUEST_MATCHING) {
      return <RightRequestMatchingMessage message={item}/>
    } else if (item.messageType === Chatting.MessageType.ACCEPT_MATCHING) {
      return <RightAcceptMatchingMessage message={item}/>
    }

    return <RightTextMessage message={item}/>
  }
}

export default ChattingRoomMessage;
