import React from "react";
import { FlatList, SafeAreaView, Text, TextInput, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { useNavigation } from "@react-navigation/native";
import AppHeader from "../../components/AppHeader";
import ChattingRoomListItem from "./components/ChattingRoomListItem";
import CommonNodata from "../../components/CommonNodata";

const ChattingRoomScreen = () => {
  const navigation = useNavigation<any>();
  return <SafeAreaView style={{
    justifyContent: 'space-between',
    flex: 1
  }}>
    <FlatList
      data={[1,2,3]}
      keyExtractor={(item) => item.toString()}
      renderItem={({ item }) => {
        return <ChattingRoomMessageList item={item} />;
      }}
      showsVerticalScrollIndicator={false}
      ListEmptyComponent={() => <CommonNodata />}
      ListHeaderComponent={() => (
        <AppHeader title={'천왕님짱'} iconName={"arrow-left"} />
      )}
      ListFooterComponent={() => <View style={{ height: 30}}/>}
    />
    <View style={{
      flexDirection: 'row',
      justifyContent: 'space-between',
      paddingHorizontal: 8,
      backgroundColor: '#e9e9e9',
    }}>
      <MaterialCommunityIcons name={'add'}/>
      <TextInput placeholder={'메세지를 입력하세요.'}/>
      <MaterialCommunityIcons name={'send'}/>
    </View>
  </SafeAreaView>
}

type ChattingRoomMessageListProps = {
  item: any
};

const ChattingRoomMessageList = ({item}: ChattingRoomMessageListProps) => {
  return (
    <View style={{
      alignSelf: 'flex-end'
    }}>
      <Text>hi</Text>
    </View>
  )
}

export default ChattingRoomScreen;
