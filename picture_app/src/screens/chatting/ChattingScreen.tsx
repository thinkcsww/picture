import React from "react";
import { FlatList, SafeAreaView, Text, View } from "react-native";
import CommonNodata from "../../components/CommonNodata";
import ChattingRoomListItem from "./components/ChattingRoomListItem";

const ChattingScreen = () => {

  return <SafeAreaView style={{
    flex: 1
  }}>
    <FlatList
      contentContainerStyle={{
        flexGrow: 1
      }}
      data={[1,2,3]}
      keyExtractor={(item) => item.toString()}
      renderItem={({ item }) => {
        return <ChattingRoomListItem item={item} />;
      }}
      showsVerticalScrollIndicator={false}
      ListEmptyComponent={() => <CommonNodata />}
      ListHeaderComponent={() => (
        <View style={{
          padding: 12,
          borderBottomColor: 'black',
          borderBottomWidth: 1,
          marginBottom: 8
        }}>
          <Text style={{
            fontSize: 26,
            color: 'black'
          }}>채팅</Text>
        </View>
      )}
      ListFooterComponent={() => <View style={{ height: 30}}/>}
    />
  </SafeAreaView>
}

export default ChattingScreen;
