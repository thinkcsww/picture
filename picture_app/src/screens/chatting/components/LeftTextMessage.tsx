import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Colors } from "../../../colors";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";
import ImageWithPH from "../../../components/ImageWithPH";

type LeftTextMessageProps = {
  message: Chatting.ChattingMessage,
  onClickProfile: () => void,
  roomInfo: Chatting.ChattingRoom,
};

const LeftTextMessage = ({ message, onClickProfile, roomInfo }: LeftTextMessageProps) => {
  return (
    <View style={{
      flexDirection: "row",
      marginLeft: 12,
      maxWidth: "70%",
    }}>
      <TouchableOpacity onPress={onClickProfile}>
        <ImageWithPH styles={{
          width: 36,
          height: 36,
          borderRadius: 18
        }} fileName={roomInfo.opponent.fileName}/>
      </TouchableOpacity>

      <View style={{
        backgroundColor: "#d9d9d9",
        paddingHorizontal: 16,
        paddingVertical: 12,
        borderRadius: 12,
        marginLeft: 6,
        flexDirection: "row",
      }}>
        <Text>{message.message}</Text>
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

export default LeftTextMessage;
