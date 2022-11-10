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
import uuid from "react-native-uuid";
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { Chatting } from "../../types/Chatting";
import { useAppSelector } from "../../store/config";
import AsyncStorageService from "../../services/AsyncStorageService";
import { Auth } from "../../types/Auth";

const ChattingRoomScreen = ({ route }: any) => {
  const [roomInfo, setRoomInfo] = useState<any>({ id: undefined });
  const [messages, setMessages] = useState<Chatting.ChattingMessage[]>([]);
  const [lastMessage, setLastMessage] = useState<Chatting.ChattingMessage>();
  const [text, setText] = useState("");
  const { user } = useAppSelector(state => state.common);
  const { targetUserId, targetUserName } = route.params;

  const navigation = useNavigation<any>();

  const getRoomWithTargetUserIdQuery = useQuery(ChattingService.QueryKey.getRoom, () => {
    return ChattingService.getRoom(targetUserId);
  }, {
    onSuccess: (result: any) => {
      console.log("==== ChattingRoom with targetUserId 조회 성공 ====");
      console.log(result);
      setRoomInfo(result);
      setMessages(result.messages?.content)
    },
    onError: (err: AxiosError) => {
      console.log("==== ChattingRoom with targetUserId 조회 실패 ====");
      console.log(err);
      setRoomInfo({ id: uuid.v4() as string });
    },
    retry: false,
  });

  const stompClient = useRef<Client>(new Client());

  useEffect(() => {
    initWebSocket().then();
  }, [roomInfo]);

  const initWebSocket = async () => {
    const token: Auth.MyOAuth2Token = await AsyncStorageService.getObjectData(AsyncStorageService.Keys.TokenInfo);
    stompClient.current.configure({
      brokerURL: "http://localhost:8080/ws",
      connectHeaders: {
        "Authorization": `Bearer ${token.access_token}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      logRawCommunication: false,
      webSocketFactory: () => {
        return SockJS("http://localhost:8080/ws");
      },
      debug: (str) => {
        console.log(str)
      },
      onConnect: (frame) => {
        console.log("==== Connected ==== ");

        stompClient.current!.subscribe(`/room/${roomInfo.id}`, (message: IMessage) => {
          console.log("message: ", message.body);
          const m = JSON.parse(message.body);
          setLastMessage(m);
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
  };

  useEffect(() => {
    if (lastMessage) {
      setMessages([...messages, lastMessage]);
    }
  }, [lastMessage])


  const onPressSend = () => {
    if (text.trim().length > 0) {
      stompClient.current!.publish({
        destination: "/api/v1/chat/send",
        body: JSON.stringify({
          message: text,
          roomId: roomInfo.id,
          senderId: user.id,
          userIdList: roomInfo.new ? [user.id, targetUserId] : undefined,
        }),
      });
      setText("");
    }
  };

  return <SafeAreaView style={{
    flex: 1,
  }}>
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={{
        flex: 1,
      }}>
      <AppHeader title={roomInfo?.opponentNickname} iconName={"arrow-left"} />
      <FlatList
        contentContainerStyle={{
          flexGrow: 1,
        }}
        data={messages}
        keyExtractor={(item, index) => index.toString()}
        renderItem={({ item, index }) => {
          return <ChattingRoomMessage item={item} />;
        }}
        ItemSeparatorComponent={() => <View style={{ height: 10 }} />}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={() => <CommonNodata />}
      />
      <View style={{
        flexDirection: "row",
        paddingHorizontal: 8,
        backgroundColor: "#e9e9e9",
        height: 50,
        alignItems: "center",
      }}>
        <TouchableOpacity>
          <MaterialCommunityIcons name={"plus"} size={20} color={Colors.GRAY_TEXT} />
        </TouchableOpacity>
        <View style={{
          flex: 1,
          backgroundColor: "#c8c8c8",
          borderRadius: 12,
          justifyContent: "center",
          paddingHorizontal: 8,
          marginHorizontal: 8,
        }}>
          <TextInput style={{
            minHeight: 30,
          }} multiline={true}
                     placeholder={"메세지를 입력하세요."}
                     onChangeText={setText}
                     value={text}
          />
        </View>

        <TouchableOpacity disabled={text.trim().length === 0} onPress={onPressSend}>
          <MaterialCommunityIcons name={"arrow-right"} size={20} color={"#808080"} />
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  </SafeAreaView>;
};


export default ChattingRoomScreen;
