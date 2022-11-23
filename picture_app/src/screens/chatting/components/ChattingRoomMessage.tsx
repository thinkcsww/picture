import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Colors } from "../../../colors";
import { useAppSelector } from "../../../store/config";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";

type ChattingRoomMessageListProps = {
  item: Chatting.ChattingMessage
};

const ChattingRoomMessage = ({item}: ChattingRoomMessageListProps) => {
  const { user } = useAppSelector(state => state.common);

  const onClickProfile = () => {

  }

  if (item.senderId !== user.id) {
    if (item.messageType === Chatting.MessageType.MESSAGE) {
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
            <Text>{item.message}</Text>
          </View>
          <View style={{
            alignSelf: 'flex-end',
            marginLeft: 4
          }}>
            <Text style={{
              fontSize: 10,
              color: Colors.GRAY_TEXT,
            }}>{DateUtils.getFormattedMessageDate(item.createdDt)}</Text>
          </View>
        </View>
      )
    } else if (item.messageType === Chatting.MessageType.REQUEST_MATCHING) {
      let message = JSON.parse(item.message);
      console.log(message);
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
마감기한: ${message.dueDate}
비용: ${message.price}
작업내용: 용왕톤으로 부탁드립니다.

의뢰를 확정하시겠습니까?`}</Text>
              <View style={{
                flexDirection: 'row',
                justifyContent: 'space-between'
              }}>
                <TouchableOpacity style={{

                }}>
                  <Text>예</Text>
                </TouchableOpacity>
                <TouchableOpacity>
                  <Text>아니오</Text>
                </TouchableOpacity>
              </View>
            </View>

          </View>
          <View style={{
            alignSelf: 'flex-end',
            marginLeft: 4
          }}>
            <Text style={{
              fontSize: 10,
              color: Colors.GRAY_TEXT,
            }}>{DateUtils.getFormattedMessageDate(item.createdDt)}</Text>
          </View>
        </View>
      )
    }
  } else {
    if (item.messageType === Chatting.MessageType.MESSAGE) {
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
            }}>{ !item.readBy ? '1' : '읽음' }</Text>
            <Text style={{
              fontSize: 10,
              color: Colors.GRAY_TEXT,
            }}>{DateUtils.getFormattedMessageDate(item.createdDt)}</Text>
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
            }}>{item.message}</Text>
          </View>
        </View>

      )
    } else if (item.messageType === Chatting.MessageType.REQUEST_MATCHING) {
      let message = JSON.parse(item.message);
      console.log(message);
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
            }}>{ !item.readBy ? '1' : '읽음' }</Text>
            <Text style={{
              fontSize: 10,
              color: Colors.GRAY_TEXT,
            }}>{DateUtils.getFormattedMessageDate(item.createdDt)}</Text>
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
            }}>{item.message}</Text>
          </View>
        </View>
      )
    }
  }
}

export default ChattingRoomMessage;
