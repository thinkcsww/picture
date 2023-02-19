import { Modal, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import React, { FC } from "react";
import { SelectValue } from "../../types/SelectValue";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import Icon from "react-native-vector-icons/Ionicons";
import { Specialty } from "../../types/Common";
import TabListHeaderTitle from "./TabListHeaderTitle";
import { Colors } from "../../colors";

type TabListHeaderSelectorProps = {
  onPress: () => void,
  selectedSpecialty: SelectValue<Specialty>,
}

const TabListHeaderSelector: FC<TabListHeaderSelectorProps> = ({ onPress, selectedSpecialty }) => {
  return (
    <TouchableOpacity style={styles.headerSelector} onPress={onPress}>
      <Text style={styles.headerSelectorTitle}>{selectedSpecialty.label}</Text>
      <MaterialCommunityIcons name={"chevron-down"} size={34} />
    </TouchableOpacity>
  );
};

type TabListHeaderSearchProps = {
  searchText: string,
  onChangeSearchText: (text: string) => void,
  onClickSearch?: () => void
}

const TabListHeaderSearch: FC<TabListHeaderSearchProps> = ({ searchText, onChangeSearchText, onClickSearch }) => {
  return (
    <TouchableOpacity style={styles.headerSearch}>
      <TouchableOpacity onPress={onClickSearch} style={{
        flexDirection: "row",
        alignItems: "center",
      }}>
        <Icon name={"md-search"} size={24} color={"#F7E54B"} />
        <Text style={styles.headerSearchTextInput}>{searchText ? searchText : "작가님 찾기"}</Text>
      </TouchableOpacity>
      {
        searchText.length > 0 && (
          <TouchableOpacity onPress={() => onChangeSearchText('')}>
            <Icon name={"close-circle-sharp"} size={24} color={"#F7E54B"} />
          </TouchableOpacity>
        )
      }

    </TouchableOpacity>
  );
};

type TabListHeaderProps = {
  onClickSelector?: () => void,
  selectedSpecialty?: SelectValue<Specialty>,
  onChangeSearchText?: (text: string) => void,
  searchText?: string,
  noOptions?: boolean,
  title?: string,
  onClickSearch?: () => void
}

const TabListHeaderWithOptions: FC<TabListHeaderProps> = ({
                                                            onClickSelector,
                                                            selectedSpecialty,
                                                            onChangeSearchText,
                                                            searchText,
                                                            noOptions,
                                                            title,
                                                            onClickSearch,
                                                          }) => {
  return (
    <View style={styles.header}>
      {onClickSelector && <TabListHeaderSelector onPress={onClickSelector} selectedSpecialty={selectedSpecialty!} />}
      {onChangeSearchText && <TabListHeaderSearch onClickSearch={onClickSearch} searchText={searchText!}
                                                  onChangeSearchText={onChangeSearchText} />}
      {noOptions && <TabListHeaderTitle title={title!} />}
    </View>
  );
};

const styles = StyleSheet.create({
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    borderBottomColor: "black",
    borderBottomWidth: 1,
    padding: 12,
    height: 70,
  },
  headerSelector: {
    flexDirection: "row",
  },
  headerSelectorTitle: {
    fontSize: 26,
    color: "black",
  },
  headerSearch: {
    borderWidth: 1,
    borderRadius: 10,
    borderColor: "#F7E54B",
    flexDirection: "row",
    padding: 8,
    alignItems: "center",
  },
  headerSearchTextInput: {
    minWidth: 80,
    marginLeft: 4,
    padding: 0,
    margin: 0,
    color: Colors.GRAY_TEXT,
  },
});

export default TabListHeaderWithOptions;
