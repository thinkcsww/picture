import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import React, { FC } from "react";
import { SelectValue } from "../../types/SelectValue";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import Icon from "react-native-vector-icons/Ionicons";
import { Specialty } from "../../types/Common";

type TabListHeaderSelectorProps = {
  onPress: () => void,
  selectedSpecialty: SelectValue<Specialty>,
}

const TabListHeaderSelector: FC<TabListHeaderSelectorProps> = ({onPress, selectedSpecialty}) => {
  return (
    <TouchableOpacity style={styles.headerSelector} onPress={onPress}>
      <Text style={styles.headerSelectorTitle}>{ selectedSpecialty.label }</Text>
      <MaterialCommunityIcons name={"chevron-down"} size={34}/>
    </TouchableOpacity>
  )
}

type TabListHeaderSearchProps = {
  searchText?: string,
  onChangeSearchText: (text: string) => void,
}

const TabListHeaderSearch: FC<TabListHeaderSearchProps> = ({searchText, onChangeSearchText}) => {
  return (
    <View style={styles.headerSearch}>
      <Icon name={"md-search"} size={24} color={'#F7E54B'}/>
      <TextInput value={searchText} onChangeText={onChangeSearchText} style={styles.headerSearchTextInput} placeholder={'작가님 찾기'}/>
    </View>
  )
}

type TabListHeaderProps = {
  onClickSelector: () => void,
  selectedSpecialty: SelectValue<Specialty>,
  onChangeSearchText?: (text: string) => void,
  searchText?: string,
}

const TabListHeader: FC<TabListHeaderProps> = ({ onClickSelector, selectedSpecialty, onChangeSearchText, searchText }) => {
  return (
    <View style={styles.header}>
      <TabListHeaderSelector onPress={onClickSelector} selectedSpecialty={selectedSpecialty}/>
      { onChangeSearchText && <TabListHeaderSearch searchText={searchText} onChangeSearchText={onChangeSearchText}/> }

    </View>
  )
}

const styles = StyleSheet.create({
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    borderBottomColor: 'black',
    borderBottomWidth: 1,
    padding: 12,
    height: 70
  },
  headerSelector: {
    flexDirection: 'row'
  },
  headerSelectorTitle: {
    fontSize: 26,
    color: 'black'
  },
  headerSearch: {
    borderWidth: 1,
    borderRadius: 10,
    borderColor: '#F7E54B',
    flexDirection: 'row',
    padding: 8,
  },
  headerSearchTextInput: {
    minWidth: 80,
    marginLeft: 4,
    padding: 0,
    margin: 0
  }
})

export default TabListHeader
