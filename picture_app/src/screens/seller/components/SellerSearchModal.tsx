import React, { FC, useState } from "react";
import { Modal, SafeAreaView, StyleSheet, Text, TextInput, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../../colors";

type SellerSearchModalProps = {
  close: () => void,
  onChangeSearchText: (text: string) => void,
}
const SellerSearchModal: FC<SellerSearchModalProps> = ({ close, onChangeSearchText }) => {

  const [searchText, setSearchText] = useState('');

  return (
    <Modal onRequestClose={close} animationType={"fade"} >
      <SafeAreaView style={styles.container}>
        <View style={{
          flexDirection: 'row',
          alignItems: 'center',
        }}>
          <MaterialCommunityIcons name={"arrow-left"} color={"black"} size={24} onPress={close} />

          <View style={styles.header}>
            <Icon name={"md-search"} size={24} color={'#F7E54B'}/>
            <TextInput
              onSubmitEditing={() => {
                onChangeSearchText(searchText);
                close();
              }}
              onChangeText={setSearchText}
              value={searchText}
              style={styles.headerSearchTextInput} placeholder={'검색어를 입력해주세요.'}/>
          </View>
        </View>

      </SafeAreaView>
    </Modal>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginHorizontal: 20
  },
  header: {
    marginLeft: 8,
    flexDirection: 'row',
    backgroundColor: 'rgba(218,218,218,0.5)',
    borderRadius: 8,
    alignItems: 'center',
    flex: 1,
    paddingVertical: 4,
    paddingHorizontal: 8
  },
  headerSearchTextInput: {
    minWidth: 80,
    marginLeft: 4,
    padding: 8,
    margin: 0,
    color: Colors.GRAY_TEXT,
    flex: 1
  }
})

export default SellerSearchModal;
