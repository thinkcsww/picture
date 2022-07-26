import React, { FC } from "react";
import { FlatList, StyleSheet, View } from "react-native";
import RequestListItem from "./RequestListItem";
import { Request } from "../../../types/Request";
import CommonNodata from "../../../components/CommonNodata";
import { Divider } from "react-native-paper";

type RequestListProps = {
  list: Request.Request[]
}

const RequestList: FC<RequestListProps> = ({ list }) => {
  return <FlatList
    data={list}
    contentContainerStyle={styles.container}
    ItemSeparatorComponent={() => <Divider style={{ height: 1, marginVertical: 20 }}/>}
    renderItem={({ item }) => {
      return <RequestListItem key={item.id} item={item}/>;
    }}

    ListEmptyComponent={() => <CommonNodata/>}
  />;
}

const styles = StyleSheet.create({
  container: {

  }
});

export default RequestList;
