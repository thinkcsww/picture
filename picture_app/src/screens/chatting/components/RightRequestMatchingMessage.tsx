import React from "react";
import { Text, View } from "react-native";
import { Colors } from "../../../colors";
import DateUtils from "../../../utils/DateUtils";
import { useAppSelector } from "../../../store/config";

type RightRequestMatchingMessageProps = {
  message: any
};

const RightRequestMatchingMessage = ({ message }: RightRequestMatchingMessageProps) => {
  const { user } = useAppSelector(state => state.common);

  const parsedMessage = JSON.parse(message.message);

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
        <Text>{`최종 의뢰서가 작성되었습니다.
마감기한: ${DateUtils.getFormattedDate(parsedMessage.dueDate)}
비용: ${parsedMessage.price}원
작업내용: 용왕톤으로 부탁드립니다.`}</Text>
      </View>
    </View>
  );
};

export default RightRequestMatchingMessage;
