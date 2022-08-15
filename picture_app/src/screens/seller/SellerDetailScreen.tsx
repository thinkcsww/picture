import React, { useState } from "react";
import { Image, ScrollView, View } from "react-native";
import Images from "../../../assets/images";
import { Divider } from "react-native-paper";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import SellerDetailProfile from "./components/SellerDetailProfile";
import SellerDetailPrice from "./components/SellerDetailPrice";
import SellerDetailNumberOfWork from "./components/SellerDetailNumberOfWork";
import SellerDetailReview from "./components/SellerDetailReview";
import DeviceInfo from 'react-native-device-info';
import { useQuery } from "react-query";
import { RequestService } from "../../services/RequestService";
import { SellerService } from "../../services/SellerService";

const SellerDetailScreen = ({ route, navigation }) => {
  const { id } = route.params;
  const [show, setShow] = useState(false);

  const onScroll = (event: any) => {
    const scrollY = event.nativeEvent.contentOffset.y;
    if (scrollY > 100) {
      if (!show) {
        setShow(true);
      }
    } else {
      if (show) {
        setShow(false);
      }
    }
  };

  // const getSellerDetailQuery = useQuery(SellerService.QueryKey.getSeller, () => {
  //   return SellerService.getRequest(id);
  // });
  //
  // if (getSellerDetailQuery.isLoading || !getSellerDetailQuery.data) {
  //   return null;
  // }
  //
  // const request = getSellerDetailQuery.data;

  return (
    <View style={{ flex: 1 }}>
      <View style={{
        position: "absolute",
        backgroundColor: show ? 'white' : "#00000000",
        paddingTop: DeviceInfo.hasNotch() ? 50 : 10,
        height: DeviceInfo.hasNotch() ? 85 : 50,
        width: "100%",
        paddingHorizontal: 20,
        zIndex: 100,
      }}>
        <MaterialCommunityIcons name={"arrow-left"} color={"black"} size={24} onPress={() => navigation.goBack()} />
      </View>
      <ScrollView
        scrollEventThrottle={16}
        onScroll={onScroll}
        contentContainerStyle={{
          paddingBottom: 50,
        }}>
        <Image source={{ uri: "" }} defaultSource={Images.profile.dummy} style={{
          height: 300,
        }} />

        <SellerDetailProfile />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailPrice />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailNumberOfWork />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailReview />


      </ScrollView>
    </View>
  );
};


export default SellerDetailScreen;
