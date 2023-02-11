import React from 'react';
import { Image, Text, View } from "react-native";
import { Colors } from "../../../colors";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";
import { Env } from "../../../constants/Env";

type RightTextMessageProps = {
  message: Chatting.ChattingMessage,
};

const RightImageMessage = ({ message }: RightTextMessageProps) => {

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
        borderRadius: 12,
        marginRight: 12,
        flexDirection: 'row',
        maxWidth: '80%'
      }}>
        <Image
          style={{
            width: 200,
            height: 150
          }}
          source={{ uri: `${Env.host}/api/v1/files/images/${message.fileName}` }}
        />
      </View>
    </View>

  )
}

export default RightImageMessage;
