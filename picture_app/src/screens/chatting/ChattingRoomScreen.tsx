import React from "react";
import { FlatList, SafeAreaView, Text, TextInput, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { useNavigation } from "@react-navigation/native";
import AppHeader from "../../components/AppHeader";
import CommonNodata from "../../components/CommonNodata";
import { Colors } from "../../colors";

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
      data={[1,2,3]}
      keyExtractor={(item) => item.toString()}
      renderItem={({ item }) => {
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

type ChattingRoomMessageListProps = {
  item: any
};

const ChattingRoomMessage = ({item}: ChattingRoomMessageListProps) => {

  if (item.sender === '') {
    return (
      <View style={{
      }}>
        <Text>hi</Text>
      </View>
    )
  } else {
    return (
      <View style={{
        alignSelf: 'flex-end',
        backgroundColor: Colors.PRIMARY,
        paddingHorizontal: 16,
        paddingVertical: 12,
        borderRadius: 12,
        marginRight: 12,
        flexDirection: 'row'
      }}>
        <Text>오전 10:10</Text>
        <Text style={{
        }}>도착했습니다 도착하시면 연락주세요^^</Text>
      </View>
    )
  }

}

export default ChattingRoomScreen;
