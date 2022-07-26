import React, { FC } from "react";
import { FlatList, StyleSheet, View } from "react-native";
import { Seller } from "../../../types/Seller";
import SellerListItem from "./SellerListItem";
import CommonNodata from "../../../components/CommonNodata";

type SellerListProps = {
  list: Seller.Seller[]
}

const SellerList: FC<SellerListProps> = ({ list }) => {
  return <FlatList
    data={list}
    contentContainerStyle={styles.container}
    ItemSeparatorComponent={() => <View style={{height: 20}}/>}
    renderItem={({ item }) => {
      return <SellerListItem key={item.id} item={item}/>;
    }}
    ListEmptyComponent={() => <CommonNodata/>}
  />;
};

const styles = StyleSheet.create({
  container: {
    paddingBottom: 30
  }
})

export default SellerList;
