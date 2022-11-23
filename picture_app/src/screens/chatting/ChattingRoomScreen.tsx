import React, { useEffect, useRef, useState } from "react";
import {
  FlatList,
  Keyboard,
  KeyboardAvoidingView,
  NativeScrollEvent,
  Platform,
  SafeAreaView,
  TextInput,
  TouchableOpacity,
  View,
} from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import AppHeader from "../../components/AppHeader";
import { Colors } from "../../colors";
import { useQuery } from "react-query";
import { AxiosError } from "axios";
import { ChattingService } from "../../services/ChattingService";
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { Chatting } from "../../types/Chatting";
import { useAppSelector } from "../../store/config";
import AsyncStorageService from "../../services/AsyncStorageService";
import { Auth } from "../../types/Auth";
import ChattingRoomMessage from "./components/ChattingRoomMessage";
import CommonNodata from "../../components/CommonNodata";
import { Env } from "../../constants/Env";
import { emptyPageResult, PageResult } from "../../types/Page";
import ChattingMessage = Chatting.ChattingMessage;
import { Specialty } from "../../types/Common";

const SCROLL_OFFSET = 0;
const ChattingRoomScreen = ({ route }: any) => {
  const [roomInfo, setRoomInfo] = useState<any>();
  const [pagedMessageList, setPagedMessageList] = useState<PageResult<Chatting.ChattingMessage>>(emptyPageResult())
  const [lastMessage, setLastMessage] = useState<Chatting.ChattingMessage>();
  const [text, setText] = useState("");
  const { user } = useAppSelector(state => state.common);
  const { targetUserId, roomType, sellerId, clientId, roomId } = route.params;
  const listRef = useRef<FlatList>(null);
  const stompClient = useRef<Client>(new Client());

  useQuery(ChattingService.QueryKey.enterRoom, () => {
    const params = {
      targetUserId: targetUserId,
      roomId: roomId ? roomId : undefined,
      sellerId: sellerId ? sellerId : undefined,
      clientId: clientId ? clientId: undefined,
    }
    return ChattingService.enterRoom(params);
  }, {
    onSuccess: (result: any) => {
      console.log("==== EnterRoom 조회 성공 ====");
      console.log(result);
      setRoomInfo(result);
      setPagedMessageList(result.messages)
    },
    onError: (err: AxiosError) => {
      console.log("==== EnterRoom 조회 실패 ====");
      console.log(err);
    },
    retry: false,
  });


  useEffect(() => {
    if (roomInfo) {
      initWebSocket().then();
    }
  }, [roomInfo]);

  useEffect(() => {
    return () => {
      stompClient.current.forceDisconnect();
      stompClient.current.deactivate().then();
    };
  }, [])

  useEffect(() => {
    const showSubscription = Keyboard.addListener("keyboardDidShow", () => {
      listRef.current!.scrollToOffset({animated: true, offset: SCROLL_OFFSET});
    });

    return () => {
      showSubscription.remove();
    };
  }, []);

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

        stompClient.current!.subscribe(`/room/${roomInfo.id}`, (message: IMessage) => {
          console.log("message: ", message.body);
          const m = JSON.parse(message.body);
          m.createdDt = new Date();
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
      console.log(lastMessage);
      if (lastMessage.messageType === Chatting.MessageType.MESSAGE || lastMessage.messageType?.includes("MATCHING")) {
        const newPagesMessageList = { ...pagedMessageList };
        newPagesMessageList.content.unshift(lastMessage);
        setPagedMessageList(newPagesMessageList);

        if (lastMessage.senderId !== user.id) {
          sendReceiveMessage(lastMessage);
        }

      } else if (lastMessage.messageType === Chatting.MessageType.ENTER || lastMessage.messageType === Chatting.MessageType.RECEIVE) {
        const newPagedMessageList = { ...pagedMessageList };
        newPagedMessageList.content.forEach(message => message.readBy = 'read');
        setPagedMessageList(newPagedMessageList);
      }

    }
  }, [lastMessage])

  const sendReceiveMessage = (lastMessage: Chatting.ChattingMessage) => {
    stompClient.current!.publish({
      destination: "/api/v1/chat/send",
      body: JSON.stringify({
        roomId: roomInfo.id,
        senderId: user.id,
        messageId: lastMessage.id,
        messageType: Chatting.MessageType.RECEIVE
      }),
    });
  }

  const onPressSend = () => {
    if (text.trim().length > 0) {
      const body: any = {
        message: text,
        roomId: roomInfo.id,
        senderId: user.id,
        roomType: roomType,
        messageType: Chatting.MessageType.MESSAGE
      };

      if (roomInfo.newRoom) {
        body.userIdList = [user.id, targetUserId]
        body.sellerId = sellerId;
        body.clientId = clientId;
      }

      stompClient.current!.publish({
        destination: "/api/v1/chat/send",
        body: JSON.stringify(body),
      });
      setText("");
    }
  };

  const getMessages = async () => {
    if (!pagedMessageList.last) {
      let result = await ChattingService.getMessages(roomInfo.id, pagedMessageList);

      const newPagedMessageList: PageResult<ChattingMessage> = { ...result, content: [...pagedMessageList.content, ...result.content ] };
      setPagedMessageList(newPagedMessageList);
    }
  }

  const onPressPlus = () => {
    if (user.sellerEnabledYN !== 'Y') return;
    const body: any = {
      roomId: roomInfo.id,
      senderId: user.id,
      roomType: roomType,
      messageType: Chatting.MessageType.REQUEST_MATCHING,
      price: 1000,
      specialty: Specialty.ETC,
      dueDate: new Date(),
      sellerId: user.id,
      clientId: roomInfo.opponent.id
    }

    stompClient.current!.publish({
      destination: "/api/v1/chat/send",
      body: JSON.stringify(body),
    });
  }


  return <SafeAreaView style={{
    flex: 1,
  }}>
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={{
        flex: 1,
      }}>
      <AppHeader title={roomInfo?.opponent?.nickname!} iconName={"arrow-left"} />
      <FlatList
        ref={listRef}
        onContentSizeChange={() => listRef.current!.scrollToOffset({animated: true, offset: SCROLL_OFFSET})}
        contentContainerStyle={{
          flexGrow: 1,
          paddingVertical: 12,
          justifyContent: 'flex-end'
        }}
        onEndReached={getMessages}
        inverted
        scrollEventThrottle={1000}
        data={pagedMessageList.content}
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
        marginBottom: Platform.OS === 'android' ? 23 : 0
      }}>
        <TouchableOpacity onPress={onPressPlus}>
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
