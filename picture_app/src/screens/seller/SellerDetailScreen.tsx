import React, { useState } from "react";
import { Image, ScrollView, View } from "react-native";
import Images from "../../../assets/images";
import { Divider } from "react-native-paper";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import SellerDetailProfile from "./components/SellerDetailProfile";
import SellerDetailPrice from "./components/SellerDetailPrice";
import SellerDetailNumberOfWork from "./components/SellerDetailNumberOfWork";
import SellerDetailRating from "./components/SellerDetailRating";
import DeviceInfo from "react-native-device-info";
import { useQuery } from "react-query";
import { SellerService } from "../../services/SellerService";
import { Seller } from "../../types/Seller";
import AppButton from "../../components/AppButton";
import { RouteNames } from "../../AppNav";
import { useAppDispatch, useAppSelector } from "../../store/config";
import { setSignUpRedux } from "../../store/slices/signUpSlice";
import { Chatting } from "../../types/Chatting";
import SellerDetailReview from "./components/SellerDetailReview";

const SellerDetailScreen = ({ route, navigation }: any) => {
  const dispatch = useAppDispatch();

  const { id } = route.params;
  const [show, setShow] = useState(false);
  const { user } = useAppSelector(state => state.common);

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


  const onClickChatting = () => {
    if (!user) {
      dispatch(setSignUpRedux( { destination : { key: RouteNames.SellerDetail, params: { id: id } }}))
      navigation.navigate(RouteNames.SignUpGuide)
    } else {
      navigation.navigate(RouteNames.ChattingRoom, { targetUserId: seller.id, sellerId: seller.id, clientId: user.id, roomType: Chatting.RoomType.PRIVATE });
    }

  }

  const getSellerDetailQuery = useQuery(SellerService.QueryKey.getSeller, () => {
    return SellerService.getSeller(id);
  }, {
    onSuccess: (result: Seller.Seller) => {
      console.log('==== Seller 상세 조회 성공 ====');
      console.log(result);
    }
  });

  if (getSellerDetailQuery.isLoading || !getSellerDetailQuery.data) {
    return null;
  }

  const seller = getSellerDetailQuery.data;

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
        showsVerticalScrollIndicator={false}
        onScroll={onScroll}
        contentContainerStyle={{
          paddingBottom: 50,
        }}>
        <Image source={{ uri: "" }} defaultSource={Images.profile.dummy} style={{
          height: 300,
        }} />

        <SellerDetailProfile onClickChatting={onClickChatting} seller={seller} />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailPrice seller={seller}/>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailNumberOfWork seller={seller} />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailRating seller={seller}/>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailReview review={seller.latestReview} sellerId={seller.id}/>

        <AppButton title={'문의하기'} onPress={onClickChatting}/>

      </ScrollView>
    </View>
  );
};


export default SellerDetailScreen;
