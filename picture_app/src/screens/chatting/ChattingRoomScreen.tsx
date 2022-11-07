import React from "react";
import {
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

const ChattingRoomScreen = () => {
  const navigation = useNavigation<any>();
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
