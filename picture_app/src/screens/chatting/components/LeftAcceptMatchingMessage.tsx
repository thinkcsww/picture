import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Colors } from "../../../colors";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";

type LeftAcceptMatchingMessageProps = {
  message: Chatting.ChattingMessage,
  onClickProfile: () => void
};

const LeftAcceptMatchingMessage = ({ message, onClickProfile }: LeftAcceptMatchingMessageProps) => {

  const parsedMessage = JSON.parse(message.message);
  return (
    <View style={{
      flexDirection: "row",
      marginLeft: 12,
      maxWidth: "70%",
    }}>
      <TouchableOpacity onPress={onClickProfile}>
        <Avatar size={"small"} source={Images.profile.dummy} rounded />
      </TouchableOpacity>

      <View style={{
        backgroundColor: "#d9d9d9",
        paddingHorizontal: 16,
        paddingVertical: 12,
        borderRadius: 12,
        marginLeft: 6,
        flexDirection: "row",
      }}>
        <Text>{`의뢰를 수락하였습니다.
마감기한: ${DateUtils.getFormattedDate(parsedMessage.dueDate)}
비용: ${parsedMessage.price}원
작업내용: 용왕톤으로 부탁드립니다.`}</Text>
      </View>
      <View style={{
        alignSelf: "flex-end",
        marginLeft: 4,
      }}>
        <Text style={{
          fontSize: 10,
          color: Colors.GRAY_TEXT,
        }}>{DateUtils.getFormattedMessageDate(message.createdDt)}</Text>
      </View>
    </View>
  );
};

export default LeftAcceptMatchingMessage;
