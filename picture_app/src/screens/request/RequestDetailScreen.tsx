import React from "react";
import { Platform, SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { useNavigation } from "@react-navigation/native";
import { Avatar } from "@rneui/themed";
import Images from "../../../assets/images";
import { Divider } from "react-native-paper";
import { Colors } from "../../colors";
import Icon from "react-native-vector-icons/Ionicons";
import DateUtils from "../../utils/DateUtils";

const RequestDetailScreen = () => {
  const navigation = useNavigation<any>();
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
            <Avatar size={"large"} source={Images.profile.dummy} rounded />
            <Text style={{
              marginLeft: 12,
              fontWeight: "bold",
              fontSize: 16,
            }}>천왕님짱</Text>
          </View>
          <Text style={{
            fontWeight: "bold",
            fontSize: 16,
          }}>채택률 80%</Text>
        </View>

        <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 20 }} />

        <View style={{
          paddingHorizontal: 12,
        }}>
          <Text style={{
            fontWeight: "bold",
            fontSize: 22,
          }}>인생샷 만들어줄 작가님 찾아요</Text>
          <View style={{
            backgroundColor: Colors.PRIMARY,
            paddingVertical: 8,
            paddingHorizontal: 14,
            width: 60,
            borderRadius: 8,
            alignItems: "center",
            marginTop: 12,
          }}>
            <Text>인물</Text>
          </View>
          <Text style={{
            marginTop: 24,
            marginBottom: 50,
            lineHeight: 20,
            fontSize: 16,
          }}>
            다리길이 늘려주시고 눈 티안나게 키워주세요 프사로 써야되는데 피부 톤 맑은 용왕님색으로 해주세요 그리고 최대한 티 안나게해야지 친구들이 안놀리니까 묵형 용왕님 색깔로 최대한 맞춰주세요 빠른 작업
            가능한 분 원해요 ^^
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
              }}>채팅 1 조회 450</Text>
              <View style={{
                flexDirection: "row",
                alignItems: "center",
              }}>
                <Icon name={"md-alarm-outline"} size={20} />
                <Text style={{
                  marginLeft: 8,
                  fontWeight: "bold",
                  fontSize: 12,
                }}>마감까지 {DateUtils.getRemainTime(new Date().toISOString())} 남음</Text>
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

        <View style={{
          paddingHorizontal: 12,
        }}>
          <Text style={{
            fontWeight: "bold",
            fontSize: 16,
          }}>천왕님짱의 다른 의뢰</Text>

          <View style={{
            marginTop: 12,
            flexDirection: 'row'
          }}>
            <TouchableOpacity style={styles.anotherRequest}>
              <Text
                numberOfLines={1}
                style={{
                  fontWeight: 'bold',
                  fontSize: 16,
                  marginBottom: 35
                }}>용왕님 색으로 수정해주세요</Text>
              <Text style={{
                fontSize: 12,
                marginBottom: 4
              }}>
                희망금액: 4000원
              </Text>
              <Text style={{
                fontSize: 12
              }}>마감까지 12시간 1{DateUtils.getRemainTime(new Date().toISOString())}</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.anotherRequest}>
              <Text
                numberOfLines={1}
                style={{
                  fontWeight: 'bold',
                  fontSize: 16,
                  marginBottom: 35
                }}>용왕님 색으로 수정해주세요</Text>
              <Text style={{
                fontSize: 12,
                marginBottom: 4
              }}>
                희망금액: 4000원
              </Text>
              <Text style={{
                fontSize: 12
              }}>마감까지 12시간 1{DateUtils.getRemainTime(new Date().toISOString())}</Text>
            </TouchableOpacity>
          </View>

        </View>

        <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginTop: 40, marginBottom: 20 }} />

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
            }}>4000원</Text>
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

const styles = StyleSheet.create({
  anotherRequest: {
    borderWidth: 1,
    borderColor: 'black',
    marginHorizontal: 8,
    width: '45%',
    height: 120,
    padding: 12,
    borderRadius: 8
  }
})

export default RequestDetailScreen;
