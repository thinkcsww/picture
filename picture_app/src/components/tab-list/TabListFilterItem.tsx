import { SelectValue } from "../../types/SelectValue";
import React, { FC } from "react";
import { StyleSheet, Text, TouchableOpacity } from "react-native";
import { Colors } from "../../colors";

type TabListFilterItemProps = {
  item: SelectValue,
  selectedFilter: SelectValue,
  onPress: (item: SelectValue) => void,
}

const TabListFilterItem: FC<TabListFilterItemProps> = ({ item, onPress, selectedFilter }) => {
  return (
    <TouchableOpacity
      onPress={() => onPress(item)}
      style={item.value === selectedFilter.value ? styles.filterSelectedItem : styles.filterNotSelectedItem}>
      <Text style={item.value === selectedFilter.value ? { color: 'black' } : { color: Colors.GRAY_TEXT }}>{item.label}</Text>
    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
  filterSelectedItem: {
    height: 40,
    marginRight: 16,
    backgroundColor: '#F7E54B60',
    borderRadius: 12,
    paddingHorizontal: 8,
    paddingVertical: 10
  },
  filterNotSelectedItem: {
    height: 40,
    marginRight: 16,
    borderRadius: 12,
    paddingHorizontal: 8,
    paddingVertical: 10
  }
})

export default TabListFilterItem
