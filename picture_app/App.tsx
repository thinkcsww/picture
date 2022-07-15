/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */
import 'react-native-gesture-handler';

import React, { useEffect, useRef, useState } from "react";
import { Alert, Button, SafeAreaView, TextInput } from "react-native";
import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { NavigationContainer } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import HomeScreen from "./src/screens/home/HomeScreen";
import SellerScreen from "./src/screens/seller/SellerScreen";
import ChattingScreen from "./src/screens/chatting/ChattingScreen";
import MyPageScreen from "./src/screens/mypage/MyPageScreen";
import RequestScreen from "./src/screens/request/RequestScreen";
import { createMaterialBottomTabNavigator } from "@react-navigation/material-bottom-tabs";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { createStackNavigator } from "@react-navigation/stack";
import SellerDetailScreen from "./src/screens/seller/SellerDetailScreen";
import AppNav from "./src/AppNav";
import AbstractHoc from "./src/AbstractHoc";
import { Provider } from "react-redux";
import { QueryClient, QueryClientProvider } from "react-query";
import store from "./src/store/config";

const App = () => {
  const stompClient = useRef<Client>();

  // const [text, setText] = useState("");


  // useEffect(() => {
  //   stompClient.current = new Client();
  //
  //   stompClient.current.configure({
  //     brokerURL: "http://192.168.200.138:8080/ws",
  //     connectHeaders: {
  //       "Authorization": "Bearer eyJhbGciOiJII1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTY1MDAzMzMsInVzZXJfbmFtZSI6IjEyMzEyMyIsImF1dGhvcml0aWVzIjpbIlVTRVJfUk9MRSJdLCJqdGkiOiJkQmRPQTlQejZQZWY0eEV3NkNoQnJnUTQyR1kiLCJjbGllbnRfaWQiOiJhcHBsb3J5Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIl19.vyfW1EQLSKhbinp9Nbc9UD491cs75OIyOxvru3K7N_E",
  //     },
  //     debug: (str) => {
  //       console.log(str);
  //     },
  //     reconnectDelay: 5000,
  //     heartbeatIncoming: 4000,
  //     heartbeatOutgoing: 4000,
  //     logRawCommunication: false,
  //     webSocketFactory: () => {
  //       return SockJS("http://192.168.200.138:8080/ws");
  //     },
  //     onConnect: (frame) => {
  //       console.log("==== Connected ==== ");
  //       const subscription = stompClient.current.subscribe("/room/1", (message: IMessage) => {
  //         console.log("message: ", message.body);
  //       });
  //     },
  //     onStompError: (err) => {
  //       Alert.alert("stomp error");
  //     },
  //     onDisconnect: (frame) => {
  //       console.log("Stomp Disconnect", frame);
  //     },
  //     onWebSocketClose: (frame) => {
  //       console.log("Stomp WebSocket Closed", frame);
  //     },
  //     onWebSocketError: (frame) => {
  //       console.log("Stomp WebSocket Error", frame);
  //     },
  //   });
  //
  //   stompClient.current.activate();
  //
  // }, []);


  const queryClient = new QueryClient();

  return (

    <Provider store={store}>
      <AbstractHoc>
        <QueryClientProvider client={queryClient}>
          <AppNav/>
        </QueryClientProvider>
      </AbstractHoc>
    </Provider>

  );
};

export default App;


// <SafeAreaView>
//   <TextInput value={text} onChangeText={(text) => setText(text)} />
//   <Button
//     title={"hi"}
//     onPress={() => {
//       stompClient.current.publish({
//         destination: "/api/v1/chat/send",
//         body: JSON.stringify({ roomId: "1", message: text }),
//       });
//     }}
//   />
// </SafeAreaView>
