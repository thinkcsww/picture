import React from "react";
import { FlatList, SafeAreaView, TextInput, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { useNavigation } from "@react-navigation/native";
import AppHeader from "../../components/AppHeader";
import CommonNodata from "../../components/CommonNodata";
import ChattingRoomMessage from "./components/ChattingRoomMessage";

const ChattingRoomScreen = () => {
  const navigation = useNavigation<any>();
  return <SafeAreaView style={{
    justifyContent: 'space-between',
    flex: 1
  }}>
    <FlatList
      ListHeaderComponent={() => (
        <AppHeader title={'천왕님짱'} iconName={"arrow-left"} />
      )}
      data={[{sender: ''}, {sender: ''}, {sender: '123'},]}
      keyExtractor={(item, index) => index.toString()}
      renderItem={({ item, index }) => {
        return <ChattingRoomMessage item={item} />;
      }}
      ItemSeparatorComponent={() => <View style={{ height: 10 }}/>}
      showsVerticalScrollIndicator={false}
      ListEmptyComponent={() => <CommonNodata />}
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



export default ChattingRoomScreen;
