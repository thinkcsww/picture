import React, { FC } from "react";
import { useNavigation } from "@react-navigation/native";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Request } from "../../../types/Request";
import { RouteNames } from "../../../AppNav";
import Icon from "react-native-vector-icons/Ionicons";
import DateUtils from "../../../utils/DateUtils";

type RequestListItemProps = {
  item: Request.Request
}
const RequestListItem: FC<RequestListItemProps> = ({ item }) => {

  const navigation = useNavigation<any>();

  const onPress = () => {
    navigation.navigate(RouteNames.RequestDetail, { id: item.id });
  };



  return (
    <TouchableOpacity onPress={onPress} style={styles.container}>
      <Text style={styles.title}>{item.title}</Text>
      <Text
        style={styles.desc}>{item.description.length > 100 ? item.description.substring(0, 100) + "..." : item.description} {item.description.length > 100 ?
        <Text>더보기</Text> : null}</Text>
      <View style={styles.infoRow}>
        <Text style={styles.priceText}>의뢰비: {item.desiredPrice ? item.desiredPrice.toLocaleString() : ""}원</Text>
        <View style={styles.dueDateRow}>
          <Icon name={"md-alarm-outline"} size={20} />
          <Text style={styles.dueDateText}>마감까지 {DateUtils.getRemainTime(item.dueDate)} 남음</Text>
        </View>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 20,
  },
  title: {
    fontWeight: "bold",
    fontSize: 18,
    marginBottom: 8,
  },
  desc: {
    lineHeight: 24,
    marginBottom: 12,
  },
  infoRow: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  priceText: {
    fontWeight: 'bold'
  },
  dueDateRow: {
    flexDirection: "row",
    alignItems: "center",
  },
  dueDateText: {
    marginLeft: 4,
    fontSize: 12
  }
});

export default RequestListItem;
