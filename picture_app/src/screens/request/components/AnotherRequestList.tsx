import React from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Request } from "../../../types/Request";
import DateUtils from "../../../utils/DateUtils";
import { Divider } from "react-native-paper";
import { useNavigation } from "@react-navigation/native";
import { RouteNames } from "../../../AppNav";

type AnotherRequestListProps = {
  request: Request.Request
}
const AnotherRequestList = ({ request }: AnotherRequestListProps) => {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
  const navigation = useNavigation<any>();

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Functions
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const onPressItem = (id: string) => {
    navigation.navigate(RouteNames.RequestDetail, { id: id });
  }

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  return (
    <>
      <View style={styles.container}>
        <Text style={styles.title}>{request.userNickname}의 다른 의뢰</Text>
        <View style={styles.list}>
          {
            request.anotherRequests.map((anotherRequest: Request.Request) => {
              return (
                <TouchableOpacity  key={anotherRequest.id} onPress={() => onPressItem(anotherRequest.id)} style={styles.item}>
                  <Text
                    numberOfLines={1}
                    style={styles.itemTitle}>{ anotherRequest.title }</Text>
                  <Text style={styles.itemPrice}>
                    희망금액: {anotherRequest.desiredPrice.toLocaleString()}원
                  </Text>
                  <Text style={styles.itemDueDate}>마감까지 {DateUtils.getRemainTime(new Date().toISOString())}</Text>
                </TouchableOpacity>
              )
            })
          }
        </View>

      </View>


      <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginTop: 40, marginBottom: 20 }} />
    </>
  )
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 12,
  },
  title: {
    fontWeight: "bold",
    fontSize: 16,
  },
  list: {
    marginTop: 12,
    flexDirection: 'row'
  },
  item: {
    borderWidth: 1,
    borderColor: 'black',
    marginHorizontal: 8,
    width: '45%',
    height: 120,
    padding: 12,
    borderRadius: 8
  },
  itemTitle: {
    fontWeight: 'bold',
    fontSize: 16,
    marginBottom: 35
  },
  itemPrice: {
    fontSize: 12,
    marginBottom: 4
  },
  itemDueDate: {
    fontSize: 12
  }
})

export default AnotherRequestList;
