import React from 'react';
import { Text, View } from "react-native";
import { Colors } from "../../../colors";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";

type RightTextMessageProps = {
  message: Chatting.ChattingMessage,
};

const RightTextMessage = ({ message }: RightTextMessageProps) => {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Functions
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  return (
    <View style={{
      alignSelf: 'flex-end',
      flexDirection: 'row',
    }}>
      <View style={{
        alignSelf: 'flex-end',
        marginRight: 4
      }}>
        <Text style={{
          fontSize: 10,
          color: Colors.GRAY_TEXT,
          textAlign: 'right'
        }}>{ !message.readBy ? '1' : '읽음' }</Text>
        <Text style={{
          fontSize: 10,
          color: Colors.GRAY_TEXT,
        }}>{DateUtils.getFormattedMessageDate(message.createdDt)}</Text>
      </View>

      <View style={{
        backgroundColor: Colors.PRIMARY,
        paddingHorizontal: 16,
        paddingVertical: 12,
        borderRadius: 12,
        marginRight: 12,
        flexDirection: 'row',
        maxWidth: '80%'
      }}>
        <Text style={{
        }}>{message.message}</Text>
      </View>
    </View>

  )
}

export default RightTextMessage;
