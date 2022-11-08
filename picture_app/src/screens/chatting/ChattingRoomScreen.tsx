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

const ChattingRoomScreen = ({ route }: any) => {
  const [roomInfo, setRoomInfo] = useState<{roomId: string}>();
  const { targetUserId } = route.params;

  const navigation = useNavigation<any>();

  const getRoomWithTargetUserIdQuery = useQuery(ChattingService.QueryKey.getRoom, () => {
    return ChattingService.getRoom(targetUserId);
  }, {
    onSuccess: (result: Seller.Seller) => {
      console.log('==== ChattingRoom with targetUserId 조회 성공 ====');
      console.log(result);

    },
    onError: (err: AxiosError) => {
      console.log('==== ChattingRoom with targetUserId 조회 실패 ====');
      console.log(err);
      setRoomInfo({ roomId: uuid.v4() as string});
    },
    retry: false
  });

  const stompClient = useRef<Client>();

  // const [text, setText] = useState("");


  useEffect(() => {
    stompClient.current = new Client();

    stompClient.current.configure({
      brokerURL: "http://192.168.200.138:8080/ws",
      connectHeaders: {
        "Authorization": "Bearer eyJhbGciOiJII1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTY1MDAzMzMsInVzZXJfbmFtZSI6IjEyMzEyMyIsImF1dGhvcml0aWVzIjpbIlVTRVJfUk9MRSJdLCJqdGkiOiJkQmRPQTlQejZQZWY0eEV3NkNoQnJnUTQyR1kiLCJjbGllbnRfaWQiOiJhcHBsb3J5Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl19.vyfW1EQLSKhbinp9Nbc9UD491cs75OIyOxvru3K7N_E",
      },
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      logRawCommunication: false,
      webSocketFactory: () => {
        return SockJS("http://192.168.200.148:8080/ws");
      },
      onConnect: (frame) => {
        console.log("==== Connected ==== ");
        const subscription = stompClient.current.subscribe("/room/1", (message: IMessage) => {
          console.log("message: ", message.body);
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

  }, []);

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
        data={[{sender: ''}, {sender: ''}, {sender: '123'},]}
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
          }} multiline={true} placeholder={'메세지를 입력하세요.'}/>
        </View>

        <TouchableOpacity>
          <MaterialCommunityIcons name={'arrow-right'} size={20} color={'#808080'}/>
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  </SafeAreaView>
}



export default ChattingRoomScreen;
