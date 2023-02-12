import React, { useEffect, useRef, useState } from "react";
import { FlatList, SafeAreaView, Text, View } from "react-native";
import CommonNodata from "../../components/CommonNodata";
import ChattingRoomListItem from "./components/ChattingRoomListItem";
import { useQuery, useQueryClient } from "react-query";
import { ChattingService } from "../../services/ChattingService";
import { AxiosError } from "axios";
import { Chatting } from "../../types/Chatting";
import TabListHeaderWithOptions from "../../components/tab-list/TabListHeaderWithOptions";
import { Auth } from "../../types/Auth";
import AsyncStorageService from "../../services/AsyncStorageService";
import { Env } from "../../constants/Env";
import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";
import { useAppSelector } from "../../store/config";

const ChattingScreen = () => {
  const stompClient = useRef<Client>(new Client());
  const { user } = useAppSelector(state => state.common);
  const [rooms, setRooms] = useState<Chatting.ChattingRoom[]>([]);
  const [newMessage, setNewMessage] = useState<any>();

  useEffect(() => {
    initWebSocket().then();
  }, []);

  useEffect(() => {
    if (newMessage) {
      let roomIndex = rooms.findIndex(room => room.id === newMessage.roomId);

      if (roomIndex > -1) {
        const newRooms = [...rooms];
        newRooms[roomIndex].lastMessage = newMessage;
        setRooms(newRooms);
      }
    }

  }, [newMessage]);

  useQuery(ChattingService.QueryKey.getRooms, () => {
    return ChattingService.getRooms();
  }, {
    onSuccess: (result: any) => {
      console.log("==== getRooms 조회 성공 ====");
      console.log(result.data);
      setRooms(result.data);
    },
    onError: (err: AxiosError) => {
      console.log("==== getRooms 조회 실패 ====");
      console.log(err);
    },
    retry: false,
  });


  const initWebSocket = async () => {
    const token: Auth.MyOAuth2Token = await AsyncStorageService.getObjectData(AsyncStorageService.Keys.TokenInfo);
    stompClient.current.configure({
      brokerURL: `${Env.host}/ws`,
      connectHeaders: {
        "Authorization": `Bearer ${token.access_token}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      logRawCommunication: false,
      webSocketFactory: () => {
        return SockJS(`${Env.host}/ws`);
      },
      debug: (str) => {
        console.log(str)
      },
      onConnect: (frame) => {
        console.log("==== Connected ==== ");

        stompClient.current!.subscribe(`/chat-list/${user.id}`, (message: IMessage) => {
          const m = JSON.parse(message.body);
          m.createdDt = new Date();

          setNewMessage(m);

          console.log(m);
        });

      },
      onStompError: (err) => {
        console.log("Stomp Error", err);
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

  return (
    <SafeAreaView style={{
      flex: 1,
    }}>
      <FlatList
        contentContainerStyle={{
          flexGrow: 1,
        }}
        data={rooms}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => {
          return <ChattingRoomListItem item={item} />;
        }}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={() => <CommonNodata />}
        ListHeaderComponent={() => <TabListHeaderWithOptions title={'채팅'} noOptions/>}
        ListFooterComponent={() => <View style={{ height: 30 }} />}
      />
    </SafeAreaView>
  );
};

export default ChattingScreen;
