import React from "react";
import { FlatList, SafeAreaView, Text, View } from "react-native";
import CommonNodata from "../../components/CommonNodata";
import ChattingRoomListItem from "./components/ChattingRoomListItem";
import { useQuery } from "react-query";
import { ChattingService } from "../../services/ChattingService";
import { AxiosError } from "axios";
import { Chatting } from "../../types/Chatting";
import TabListHeaderWithOptions from "../../components/tab-list/TabListHeaderWithOptions";

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
        ListHeaderComponent={() => <TabListHeaderWithOptions title={'채팅'} noOptions/>}
        ListFooterComponent={() => <View style={{ height: 30 }} />}
      />
    </SafeAreaView>
  );
};

export default ChattingScreen;
