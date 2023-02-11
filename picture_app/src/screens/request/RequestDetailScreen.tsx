import React from "react";
import { Platform, SafeAreaView, ScrollView, Text, TouchableOpacity, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { useNavigation } from "@react-navigation/native";
import { Avatar } from "@rneui/themed";
import Images from "../../../assets/images";
import { Divider } from "react-native-paper";
import { Colors } from "../../colors";
import Icon from "react-native-vector-icons/Ionicons";
import DateUtils from "../../utils/DateUtils";
import { useQuery } from "react-query";
import { RequestService } from "../../services/RequestService";
import CommonUtils from "../../utils/CommonUtils";
import AnotherRequestList from "./components/AnotherRequestList";
import { Env } from "../../constants/Env";

const RequestDetailScreen = ({ route }: any) => {
  const { id } = route.params;
  const navigation = useNavigation<any>();

  const getRequestDetailQuery = useQuery(RequestService.QueryKey.getRequest, () => {
    return RequestService.getRequest(id);
  });

  if (getRequestDetailQuery.isLoading || !getRequestDetailQuery.data) {
    return null;
  }

  const request = getRequestDetailQuery.data;

  return (
    <SafeAreaView style={{flex: 1}}>
      <View style={{
        backgroundColor: 'white',
        width: '100%',
        paddingHorizontal: 20,
        paddingVertical: 10,
        zIndex: 100
      }}>
        <MaterialCommunityIcons name={"arrow-left"} color={"black"} size={24} onPress={() => navigation.goBack()} />
      </View>
      <ScrollView contentContainerStyle={{
        paddingBottom: 50,
      }}>
        <View style={{
          flexDirection: "row",
          justifyContent: "space-between",
          marginTop: Platform.OS === "ios" ? 20 : 20,
          paddingHorizontal: 12,
          alignItems: "center",
        }}>
          <View style={{
            flexDirection: "row",
            alignItems: "center",
          }}>
            <Avatar size={"large"}  source={{ uri: `${Env.host}/api/v1/files/images/${request.userProfileFileName}` }} rounded />
            <Text style={{
              marginLeft: 12,
              fontWeight: "bold",
              fontSize: 16,
            }}>{request.userNickname}</Text>
          </View>
          <Text style={{
            fontWeight: "bold",
            fontSize: 16,
          }}>{ request.userAcceptRate === -1 ? '채택률 미정 ' : `채택률 ${request.userAcceptRate}%` } </Text>
        </View>

        <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 20 }} />

        <View style={{
          paddingHorizontal: 12,
        }}>
          <Text style={{
            fontWeight: "bold",
            fontSize: 22,
          }}>{ request.title }</Text>
          <View style={{
            backgroundColor: Colors.PRIMARY,
            paddingVertical: 8,
            paddingHorizontal: 14,
            width: 60,
            borderRadius: 8,
            alignItems: "center",
            marginTop: 12,
          }}>
            <Text>{ CommonUtils.specialtyToLabel(request.specialty) }</Text>
          </View>
          <Text style={{
            marginTop: 24,
            marginBottom: 50,
            lineHeight: 20,
            fontSize: 16,
          }}>
            { request.description }
          </Text>
          <View style={{
            flexDirection: "row",
            flex: 1,
          }}>
            <View style={{
              flexDirection: "row",
              flex: 1,
              justifyContent: "space-between",
            }}>
              <Text style={{
                color: "#757575",
              }}>채팅 { request.chatCount.toLocaleString() } 조회 { request.readCount.toLocaleString() }</Text>
              <View style={{
                flexDirection: "row",
                alignItems: "center",
              }}>
                <Icon name={"md-alarm-outline"} size={20} />
                <Text style={{
                  marginLeft: 8,
                  fontWeight: "bold",
                  fontSize: 12,
                }}>마감까지 {DateUtils.getRemainTime(new Date(request.dueDate).toISOString())} 남음</Text>
              </View>
            </View>
          </View>
        </View>

        <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 20 }} />

        <TouchableOpacity style={{
          paddingHorizontal: 12,
          flexDirection: "row",
          justifyContent: "space-between",
        }}>
          <Text style={{
            fontSize: 16,
            fontWeight: "bold",
          }}>이 게시글 신고하기</Text>
          <MaterialCommunityIcons name={"chevron-right"} size={24} />
        </TouchableOpacity>

        <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 20 }} />

        {
          request.anotherRequests.length > 0 ? <AnotherRequestList request={request}/> : null
        }


        <View style={{
          flexDirection: 'row',
          paddingHorizontal: 12,
          alignItems: 'center',
          justifyContent: 'space-between'
        }}>
          <View style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
            <TouchableOpacity style={{
              borderRightWidth: 1,
              borderRightColor: '#e9e9e9',
              padding: 12
            }}>
              <Icon name={'ios-heart'} size={28} color={Colors.PRIMARY}/>
            </TouchableOpacity>
            <Text style={{
              fontWeight: 'bold',
              marginLeft: 12,
              fontSize: 16,
            }}>{ request.desiredPrice.toLocaleString() }원</Text>
          </View>

          <TouchableOpacity style={{
            backgroundColor: Colors.PRIMARY,
            paddingVertical: 10,
            paddingHorizontal: 20,
            borderRadius: 8
          }}>
            <Text style={{
              color: 'white',
              fontWeight: 'bold'
            }}>채팅하기</Text>
          </TouchableOpacity>
        </View>


      </ScrollView>
    </SafeAreaView>
  );
};



export default RequestDetailScreen;
