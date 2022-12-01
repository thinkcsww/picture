import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Colors } from "../../../colors";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";

type LeftTextMessageProps = {
  message: Chatting.ChattingMessage,
  onClickProfile: () => void
};

const LeftTextMessage = ({ message, onClickProfile }: LeftTextMessageProps) => {
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
