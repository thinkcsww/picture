import React from "react";
import { FlatList, SafeAreaView, Text, View } from "react-native";
import CommonNodata from "../../components/CommonNodata";
import ChattingRoomListItem from "./components/ChattingRoomListItem";
import { useQuery } from "react-query";
import { ChattingService } from "../../services/ChattingService";
import { AxiosError } from "axios";
import { Chatting } from "../../types/Chatting";

const ChattingScreen = () => {

  let getRoomsQuery = useQuery(ChattingService.QueryKey.getRooms, () => {
    return ChattingService.getRooms();
  }, {
    onSuccess: (result: any) => {
      console.log("==== getRooms 조회 성공 ====");
      console.log(result);
    },
    onError: (err: AxiosError) => {
      console.log("==== getRooms 조회 실패 ====");
      console.log(err);
    },
    retry: false,
  });

  const rooms: Chatting.ChattingRoom[] = getRoomsQuery.data;
  console.log(getRoomsQuery);

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
        ListHeaderComponent={() => (
          <View style={{
            padding: 12,
            borderBottomColor: "black",
            borderBottomWidth: 1,
            marginBottom: 8,
          }}>
            <Text style={{
              fontSize: 26,
              color: "black",
            }}>채팅</Text>
          </View>
        )}
        ListFooterComponent={() => <View style={{ height: 30 }} />}
      />
    </SafeAreaView>
  );
};

export default ChattingScreen;
