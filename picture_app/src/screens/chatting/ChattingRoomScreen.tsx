import React, { useEffect, useRef, useState } from "react";
import {
  Alert,
  FlatList,
  KeyboardAvoidingView,
  Platform,
  SafeAreaView,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { useNavigation } from "@react-navigation/native";
import AppHeader from "../../components/AppHeader";
import CommonNodata from "../../components/CommonNodata";
import ChattingRoomMessage from "./components/ChattingRoomMessage";
import { Colors } from "../../colors";
import { useQuery } from "react-query";
import { Seller } from "../../types/Seller";
import { AxiosError } from "axios";
import { ChattingService } from "../../services/ChattingService";
import uuid from 'react-native-uuid';
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { Chatting } from "../../types/Chatting";

const ChattingRoomScreen = ({ route }: any) => {
  const [roomInfo, setRoomInfo] = useState<{roomId?: string}>({roomId: undefined});
  const [messages, setMessages] = useState<Chatting.ChattingMessage[]>([]);

  const { targetUserId } = route.params;

  const navigation = useNavigation<any>();

  const getRoomWithTargetUserIdQuery = useQuery(ChattingService.QueryKey.getRoom, () => {
    return ChattingService.getRoom(targetUserId);
  }, {
    onSuccess: (result: Seller.Seller) => {
      console.log('==== ChattingRoom with targetUserId 조회 성공 ====');
      console.log(result);
      setRoomInfo({
        roomId: result.id
      })
    },
    onError: (err: AxiosError) => {
      console.log('==== ChattingRoom with targetUserId 조회 실패 ====');
      console.log(err);
      setRoomInfo({ roomId: uuid.v4() as string});
    },
    retry: false
  });

  const stompClient = useRef<Client>();

  const [text, setText] = useState("");


  useEffect(() => {
    if (roomInfo.roomId) {
      stompClient.current = new Client();

      stompClient.current.configure({
        brokerURL: "http://localhost:8080/ws",
        connectHeaders: {
          "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NjgwODc3MjcsInVzZXJfbmFtZSI6IjIyNjYwMzU5MDUiLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiZ1gxaDJsMkJVOEw3b0REZnNLU1JIenhJb3dJIiwiY2xpZW50X2lkIjoiYXBwbG9yeSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.ZiPN_wXbk4myFNdvuyvbBLSQBj7JXoKKdlRwRxZsXUA",
        },
        debug: (str) => {
          console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        logRawCommunication: false,
        webSocketFactory: () => {
          return SockJS("http://localhost:8080/ws");
        },
        onConnect: (frame) => {
          console.log("==== Connected ==== ");
          const subscription = stompClient.current!.subscribe(`/room/${roomInfo.roomId}`, (message: IMessage) => {
            console.log("message: ", message.body);
            const newMessages = [...messages];
            newMessages.push(JSON.parse(message.body));
            console.log(newMessages.length);
            setMessages(newMessages);
          });
        },
        onStompError: (err) => {
          console.log("Stomp Error", err)
          // Alert.alert("stomp error");
        },
        onDisconnect: (frame) => {
          console.log("Stomp Disconnect", frame);
        },
        onWebSocketClose: (frame) => {
          console.log("Stomp WebSocket Closed", frame);
        },
        onWebSocketError: (frame) => {
          console.log("Stomp WebSocket Error", frame);
        },
      });

      stompClient.current.activate();
    }
  }, [roomInfo]);

  const onClickSend = () => {
    stompClient.current!.publish({
      destination: '/api/v1/chat/send',
      body: JSON.stringify({message: text, roomId: roomInfo.roomId, senderId: 'ad98b365-dc76-4fac-a034-66801b9deb65', userIdList: ['ad98b365-dc76-4fac-a034-66801b9deb65', targetUserId]})
    })
  }

  return <SafeAreaView style={{
    flex: 1
  }}>
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={{
        flex: 1
    }}>
      <FlatList
        ListHeaderComponent={() => (
          <AppHeader title={'천왕님짱'} iconName={"arrow-left"} />
        )}
        contentContainerStyle={{
          flexGrow: 1,
        }}
        data={messages}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({ item, index }) => {
          return <ChattingRoomMessage item={item} />;
        }}
        ItemSeparatorComponent={() => <View style={{ height: 10 }}/>}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={() => <CommonNodata />}
      />
      <View style={{
        flexDirection: 'row',
        paddingHorizontal: 8,
        backgroundColor: '#e9e9e9',
        height: 50,
        alignItems: 'center'
      }}>
        <TouchableOpacity>
          <MaterialCommunityIcons name={'plus'} size={20} color={Colors.GRAY_TEXT}/>
        </TouchableOpacity>
        <View style={{
          flex: 1,
          backgroundColor: '#c8c8c8',
          borderRadius: 12,
          justifyContent: 'center',
          paddingHorizontal: 8,
          marginHorizontal: 8
        }}>
          <TextInput style={{
            minHeight: 30
          }} multiline={true}
             placeholder={'메세지를 입력하세요.'}
                     onChangeText={setText}
          />
        </View>

        <TouchableOpacity onPress={onClickSend}>
          <MaterialCommunityIcons name={'arrow-right'} size={20} color={'#808080'}/>
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  </SafeAreaView>
}



export default ChattingRoomScreen;
