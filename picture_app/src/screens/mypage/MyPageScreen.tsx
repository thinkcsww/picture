import React, { useEffect, useState } from "react";
import { FlatList, Platform, SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import AsyncStorageService from "../../services/AsyncStorageService";
import UserService from "../../services/UserService";
import { useAppDispatch } from "../../store/config";
import { setUser } from "../../store/slices/commonSlice";
import TabListHeaderWithOptions from "../../components/tab-list/TabListHeaderWithOptions";
import { Divider } from "react-native-paper";
import { Colors } from "../../colors";
import DateUtils from "../../utils/DateUtils";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { launchImageLibrary } from "react-native-image-picker";
import { instance } from "../../hooks/useAxiosLoader";
import { Env } from "../../constants/Env";
import ImageWithPH from "../../components/ImageWithPH";
import { useNavigation } from "@react-navigation/native";
import { RouteNames } from "../../AppNav";

const MyPageScreen = () => {
  const dispatch = useAppDispatch();

  const [userDetail, setUserDetail] = useState<any>();
  const navigation = useNavigation<any>();

  useEffect(() => {
    getUserMe().then();
  }, []);

  const getUserMe = async () => {
    UserService.getUserMe().then((res: any) => {
      console.log("==== 마이페이지 getUserMe ====");
      console.log(res);
      setUserDetail(res.data);
      // dispatch(setUser(res))
    });
  };

  const onPressEditProfileImage = () => {
    launchImageLibrary({
      includeBase64: true,
      mediaType: "photo",
    }).then((res) => {
      if (!res.didCancel) {
        if (res!.assets!.length > 0) {
          const image = res.assets![0];
          const formData = new FormData();
          formData.append("attachFile", {
            name: image.fileName,
            type: image.type,
            uri:
              Platform.OS === "android"
                ? image.uri!
                : image.uri!.replace("file://", ""),
          });

          instance.post(`${Env.host}/api/v1/users/${userDetail.id}/profile-image`, formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          })
            .then((response) => {
              console.log("=== 성공 ===");
              console.log(response.data);
              getUserMe().then();
            })
            .catch((error) => {
              console.log("=== 에러 ===");
              console.log(error);
            });
        }
      }

    });
  };

  if (!userDetail) return null;

  const logout = async () => {
    await AsyncStorageService.removeData(AsyncStorageService.Keys.TokenInfo);
    dispatch(setUser(undefined));
  };

  const onClickFavoriteUser = (id: string) => {
    navigation.navigate(RouteNames.SellerDetail, { id: id })
  }

  const onClickGoToRequest = () => {
    navigation.navigate(RouteNames.AddRequest);
  }


  return <SafeAreaView style={styles.container}>
    <ScrollView>
      <TabListHeaderWithOptions title={"마이페이지"} noOptions />
      <View style={{
        paddingHorizontal: 20,
      }}>
        <TouchableOpacity onPress={onPressEditProfileImage} style={{
          marginTop: 24,
          flexDirection: "row",
          marginVertical: 12,
        }}>
          <ImageWithPH fileName={userDetail.fileName} styles={styles.profileImage} />

          <View style={{
            justifyContent: "center",
          }}>
            <Text style={{
              fontSize: 22,
            }}>{userDetail?.nickname}</Text>
            {/*<View style={{*/}
            {/*  flexDirection: 'row'*/}
            {/*}}>*/}
            {/*  <Text>나의 평점</Text>*/}
            {/*  <Text>4.1</Text>*/}
            {/*</View>*/}
          </View>
        </TouchableOpacity>
        {/*<View style={{*/}
        {/*  flexDirection: "row",*/}
        {/*  justifyContent: "space-between",*/}
        {/*  marginVertical: 12,*/}
        {/*}}>*/}
        {/*  <TouchableOpacity style={styles.profileBtn}>*/}
        {/*    <Text>비고</Text>*/}
        {/*  </TouchableOpacity>*/}
        {/*  <TouchableOpacity style={styles.profileBtn}>*/}
        {/*    <Text>전문가 전환하기</Text>*/}
        {/*  </TouchableOpacity>*/}
        {/*</View>*/}
      </View>
      <Divider style={{ height: 8, backgroundColor: Colors.DIMMED }} />
      <View style={{
        paddingHorizontal: 20,
        marginVertical: 12,
      }}>
        <View style={{
          marginTop: 12,
        }}>
          <Text style={{
            fontSize: 18,
            fontWeight: "500",
            marginBottom: 6,
          }}>의뢰중인 작업</Text>

          {
            userDetail?.matchings?.ACCEPT && userDetail?.matchings?.ACCEPT.length > 0 ? (
              <View style={{
                backgroundColor: Colors.DIMMED,
                height: 100,
                borderRadius: 8,
                justifyContent: "center",
                alignItems: "center",
              }}>
                <Text>{userDetail?.matchings?.COMPLETE[0]?.opponentNickname} 작가님과의 작업이 진행중이에요!! </Text>
                <Text>마감기한: {DateUtils.getRemainTime(userDetail?.matchings?.COMPLETE[0]?.dueDate)}</Text>
                <Text>작업내용: {userDetail?.matchings?.COMPLETE[0]?.comment}</Text>

              </View>
            ) : (
              <View style={{
                backgroundColor: Colors.DIMMED,
                height: 100,
                borderRadius: 8,
                justifyContent: "center",
                alignItems: "center",
              }}>
                <Text style={{ color: Colors.GRAY_TEXT }}>진행중인 의뢰가 없어요!</Text>
                <TouchableOpacity onPress={onClickGoToRequest} style={{
                  flexDirection: "row",
                  alignItems: "center",
                  backgroundColor: "#dad8d8",
                  borderRadius: 8,
                  paddingHorizontal: 8,
                  marginTop: 8,
                }}>
                  <Text>의뢰하러 가기</Text>
                  <MaterialCommunityIcons style={{ color: "white" }} size={24} name={"arrow-right"} />
                </TouchableOpacity>


              </View>
            )
          }
        </View>
      </View>

      <Divider style={{ height: 8, backgroundColor: Colors.DIMMED }} />
      <View style={{
        marginVertical: 8,
        paddingHorizontal: 20
      }}>
        <Text style={{
          fontSize: 18,
          fontWeight: "500",
          marginBottom: 6,
        }}>단골 전문가 리스트</Text>
        {
          userDetail?.favoriteUsers?.length === 0 ? <View style={{
            backgroundColor: Colors.DIMMED,
            height: 100,
            borderRadius: 8,
            justifyContent: "center",
            alignItems: "center",
          }}>
            <Text>단골 전문가가 없습니다.</Text>
          </View> : <FlatList
            showsHorizontalScrollIndicator={false}
            horizontal
            data={userDetail?.favoriteUsers}
            renderItem={({item}) => {
              return <TouchableOpacity onPress={() => onClickFavoriteUser(item.userId)} key={item.id} style={{
                width: 100,
                height: 100,
                backgroundColor: Colors.DIMMED,
                marginRight: 8,
                borderRadius: 8,
                alignItems: "center",
              }}>
                <ImageWithPH styles={{
                  width: 100,
                  height: 60,
                  borderTopLeftRadius: 8,
                  borderTopRightRadius: 8,
                  marginBottom: 10
                }} fileName={item.fileName} />
                <Text>{item.nickname}</Text>
              </TouchableOpacity>
            }
            }/>
        }

      </View>


      <Divider style={{ height: 8, backgroundColor: Colors.DIMMED }} />
      <View style={{
        paddingHorizontal: 20,
      }}>
        <View style={{
          marginTop: 12,
        }}>
          <Text style={{
            fontSize: 18,
            fontWeight: "500",
            marginBottom: 6,
          }}>완료된 작업</Text>

          {
            userDetail?.matchings?.COMPLETE && userDetail?.matchings?.COMPLETE.length > 0 ? (
              <View style={{
                backgroundColor: Colors.DIMMED,
                height: 100,
                borderRadius: 8,
                justifyContent: "center",
                alignItems: "center",
              }}>
                <Text>{userDetail?.matchings?.COMPLETE[0]?.opponentNickname} 작가님과의 작업 </Text>
                <Text>완료일: {DateUtils.getFormattedDate(userDetail?.matchings?.COMPLETE[0]?.completeDt)}</Text>
                <Text>작업내용: {userDetail?.matchings?.COMPLETE[0]?.comment}</Text>

              </View>
            ) : (
              <View style={{
                backgroundColor: Colors.DIMMED,
                height: 100,
                borderRadius: 8,
                justifyContent: "center",
                alignItems: "center",
              }}>
                <Text>완료된 작업이 없습니다.</Text>
              </View>
            )
          }
        </View>
      </View>
    </ScrollView>
  </SafeAreaView>;
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  profileImage: {
    width: 80,
    height: 80,
    borderRadius: 8,
    backgroundColor: "red",
    marginRight: 12,
  },
  profileBtn: {
    backgroundColor: Colors.DIMMED,
    justifyContent: "center",
    alignItems: "center",
    height: 40,
    flex: 1,
    borderRadius: 8,
    marginHorizontal: 4,
  },
});

export default MyPageScreen;
