import React from "react";
import { Text, TouchableOpacity, View } from "react-native";
import { Avatar } from "@rneui/themed";
import Images from "../../../../assets/images";
import { Colors } from "../../../colors";
import { useAppSelector } from "../../../store/config";
import DateUtils from "../../../utils/DateUtils";
import { Chatting } from "../../../types/Chatting";

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

  if (item.senderId !== user.id) {
    if (item.messageType === Chatting.MessageType.REQUEST_MATCHING) {
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
마감기한: ${DateUtils.getFormattedDate(message.dueDate)}
비용: ${message.price}원
작업내용: 용왕톤으로 부탁드립니다.

의뢰를 확정하시겠습니까?`}</Text>
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
    } else if (item.messageType === Chatting.MessageType.ACCEPT_MATCHING) {

    }

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
  } else {
    if (item.messageType === Chatting.MessageType.ACCEPT_MATCHING) {
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

export default ChattingRoomMessage;
