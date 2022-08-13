import { FlatList, StyleSheet } from "react-native";
import React, { FC } from "react";
import { SelectValue } from "../../types/SelectValue";
import TabListFilterItem from "./TabListFilterItem";

type TabListFilterProps = {
  list: SelectValue[],
  selectedFilter: SelectValue,
  onPress: (item: SelectValue) => void,
}

const TabListFilter: FC<TabListFilterProps> = ({list, selectedFilter, onPress}) => {
  return (
    <FlatList
      keyExtractor={(item) => item.value}
      style={styles.filter} data={list}
      showsHorizontalScrollIndicator={false}
      horizontal
      renderItem={({ item }) => <TabListFilterItem item={item} onPress={onPress} selectedFilter={selectedFilter}/>}/>
  )
}

const styles = StyleSheet.create({
  filter: {
    height: 70,
    paddingVertical: 8,
    paddingLeft: 12,
    marginBottom: 10,
    flexGrow: 0
  }
})

export default TabListFilter;
