/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, {useEffect, useRef, useState} from 'react';
import {Alert, Button, SafeAreaView, TextInput} from 'react-native';
import {Client, IMessage, Message, Stomp} from '@stomp/stompjs';
import SockJS from "sockjs-client";

const App = () => {
    const stompClient = useRef<Client>();

    const [text, setText] = useState('');


    useEffect(() => {
        stompClient.current = new Client();

        stompClient.current.configure({
            brokerURL: 'http://192.168.200.138:8080/ws',
            connectHeaders: {},
            debug: (str) => {
                console.log(str);
            },
            reconnectDelay: 500,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            logRawCommunication: false,
            webSocketFactory: () => {
              return SockJS('http://192.168.200.138:8080/ws')
            },
            onConnect: (frame) => {
                console.log('==== Connected ==== ');
                const subscription = stompClient.current.subscribe('/room/1', (message: IMessage) =>{
                    console.log('message: ', message.body)
                });
            },
            onStompError: (err) => {
                Alert.alert('stomp error');
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
        })

        stompClient.current.activate();

    }, [])


  return (
    <SafeAreaView>
        <TextInput value={text} onChangeText={(text) => setText(text)}/>
      <Button
        title={'hi'}
        onPress={() => {
            stompClient.current.publish({
                destination: '/api/v1/chat/send',
                body: text
            })
        }}
      />
    </SafeAreaView>
  );
};

export default App;
