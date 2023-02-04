import React, { useState } from "react";
import { Image, ScrollView, Text, TouchableOpacity, View } from "react-native";
import Images from "../../../assets/images";
import { Divider } from "react-native-paper";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import SellerDetailProfile from "./components/SellerDetailProfile";
import SellerDetailPrice from "./components/SellerDetailPrice";
import SellerDetailNumberOfWork from "./components/SellerDetailNumberOfWork";
import SellerDetailReview from "./components/SellerDetailReview";
import DeviceInfo from "react-native-device-info";
import { useQuery } from "react-query";
import { SellerService } from "../../services/SellerService";
import { Seller } from "../../types/Seller";
import AppButton from "../../components/AppButton";
import { Avatar } from "@rneui/themed";
import { Colors } from "../../colors";
import Icon from "react-native-vector-icons/Ionicons";
import { RouteNames } from "../../AppNav";
import { useAppDispatch, useAppSelector } from "../../store/config";
import { setSignUpRedux } from "../../store/slices/signUpSlice";
import { Chatting } from "../../types/Chatting";
import DateUtils from "../../utils/DateUtils";
import CommonNodata from "../../components/CommonNodata";
import RatingStarIcons from "./components/RatingStarIcons";

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

        <SellerDetailProfile seller={seller} />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailPrice seller={seller}/>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailNumberOfWork seller={seller} />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <SellerDetailReview seller={seller}/>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        {

            <>

              <View style={{
                paddingHorizontal: 12,
                marginBottom: 30
              }}>
                <Text style={{
                  fontWeight: "bold",
                  color: "black",
                }}>리뷰</Text>
                {
                  seller.latestReview ? (
                    <>
                      <View style={{
                        flexDirection: 'row',
                        marginTop: 20,
                      }}>
                        <Avatar size={"small"} source={Images.profile.dummy} rounded />
                        <View style={{
                          marginLeft: 12,
                          justifyContent: 'space-between',
                          flex: 1
                        }}>
                          <Text style={{
                            fontWeight: 'bold'
                          }}>{ seller.latestReview?.writerNickname }</Text>
                          <View style={{
                            flexDirection: 'row',
                            justifyContent: 'space-between',
                          }}>
                            <View style={{
                              flexDirection: 'row',
                              alignItems: 'center'
                            }}>
                              <RatingStarIcons rateAvg={seller.rateAvg}/>
                              <Text style={{
                                marginLeft: 12,
                                color: '#b9b9b9',
                                fontSize: 12
                              }}>{ DateUtils.getFormattedDate(seller.latestReview?.createdDt) }</Text>
                            </View>

                            <TouchableOpacity>
                              <Text style={{
                                marginLeft: 12,
                                color: '#b9b9b9',
                                fontSize: 12
                              }}>신고하기</Text>
                            </TouchableOpacity>
                          </View>
                        </View>
                      </View>
                      <View style={{
                        paddingHorizontal: 12
                      }}>

                        <Text style={{
                          lineHeight: 20,
                          marginTop: 20,
                        }}>{ seller.latestReview?.content }</Text>

                        <TouchableOpacity style={{
                          marginTop: 20,
                        }}>
                          <Text style={{
                            color: '#b9b9b9',
                            fontSize: 12
                          }}>리뷰 더보기</Text>
                        </TouchableOpacity>
                      </View>
                    </>
                  ) : <CommonNodata message={'리뷰가 없습니다'} height={200}/>
                }
              </View>
            </>
          // )
        }



        <AppButton title={'문의하기'} onPress={onClickChatting}/>

      </ScrollView>
    </View>
  );
};


export default SellerDetailScreen;
