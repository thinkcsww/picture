import React, { useState } from "react";
import { SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { Divider } from "react-native-paper";
import { Colors } from "../../colors";
import AppCheckBox from "../../components/AppCheckBox";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import DatePicker from "react-native-date-picker";
import { Seller } from "../../types/Seller";
import { Specialty } from "../../types/Common";
import { useNavigation } from "@react-navigation/native";
import { Regex } from "../../utils/Regex";
import AppButton from "../../components/AppButton";
import { User } from "../../types/User";
import UserService from "../../services/UserService";
import { useAppSelector } from "../../store/config";
import { useMutation } from "react-query";
import { AxiosError } from "axios";
import { AuthService } from "../../services/AuthService";
import { Auth } from "../../types/Auth";
import AsyncStorage from '@react-native-async-storage/async-storage';
import AsyncStorageService from "../../services/AsyncStorageService";

const SignUpSellerScreen = () => {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
  const navigation = useNavigation<any>()
  const { signUpRedux } = useAppSelector(state => state.signUp);

  const signUpMutation = useMutation('', (dto: User.CreateDto) => {
    return UserService.signUp(dto);
  }, {
    onSuccess: (result: User.VM) => {
      console.log('==== 작가 회원가입 성공 ====');
      console.log(result);
      signInMutation.mutate(result.username);
    },
    onError: (e: AxiosError) => {
      console.log('==== 작가 회원가입 실패 ====');
      console.log(e.message);
    }
  });

  const signInMutation = useMutation('', (username: string) => {
    const dto = new Auth.LoginDto(username, signUpRedux.token);
    return AuthService.login(dto);
  }, {
    onSuccess: (result: Auth.MyOAuth2Token) => {
      console.log('==== 회원가입 -> 로그인 성공 ====');
      console.log(result);
      AsyncStorageService.setStringData(AsyncStorageService.Keys.AccessToken, result.access_token).then();
      AsyncStorageService.setStringData(AsyncStorageService.Keys.RefreshToken, result.refresh_token).then();
      navigation.navigate(signUpRedux.destination)
    },
    onError: (e: AxiosError) => {
      console.log('==== 회원가입 -> 로그인 실패 ====');
      console.log(e.message);
    }
  })

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const [showNicknameError, setShowNicknameError] = useState(false);
  const [nicknameChecked, setNicknameChecked] = useState(false);

  const [nickname, setNickname] = useState('')
  const [desc, setDesc] = useState('')
  const [peopleChecked, setPeopleChecked] = useState(false)
  const [bgChecked, setBgChecked] = useState(false)
  const [officialChecked, setOfficialChecked] = useState(false)
  const [peoplePrice, setPeoplePrice] = useState('')
  const [bgPrice, setBgPrice] = useState('')
  const [officialPrice, setOfficialPrice] = useState('')

  const [agreeState, setAgreeState] = useState({
    agreeAll: false,
    over14Agree: false,
    serviceTermAgree: false,
    personalInfoTermAgree: false,
    eventReceiveAgree: false,
    dormantDisableAgree: false,
  })

  const [fromDt, setFromDt] = useState(new Date())
  const [isFromOpen, setIsFromOpen] = useState(false)

  const [toDt, setToDt] = useState(new Date())
  const [isToOpen, setIsToOpen] = useState(false)

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Functions
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const onChangeNickname = (text: string) => {
    setNickname(text)
    setNicknameChecked(false);
  }

  const onClickCheckNickname = async () => {
    try {
      let result = await UserService.checkNickname(nickname);
      console.log(result);
      setShowNicknameError(false);
      setNicknameChecked(true);
    } catch (e) {
      setShowNicknameError(true);
      console.log(e);
    }
  }

  const toggleAgreeAll = () => {
    if (agreeState.agreeAll) {
      setAgreeState({
        agreeAll: false,
        over14Agree: false,
        serviceTermAgree: false,
        personalInfoTermAgree: false,
        eventReceiveAgree: false,
        dormantDisableAgree: false,
      })
    } else {
      setAgreeState({
        agreeAll: true,
        over14Agree: true,
        serviceTermAgree: true,
        personalInfoTermAgree: true,
        eventReceiveAgree: true,
        dormantDisableAgree: true,
      })
    }
  }

  const toggleServiceTerm = () => {
    if (agreeState.serviceTermAgree) {
      setAgreeState({
        ...agreeState,
        agreeAll: false,
        serviceTermAgree: !agreeState.serviceTermAgree
      })
    } else {
      let agreeAll = false;
      if (agreeState.over14Agree && agreeState.personalInfoTermAgree && agreeState.eventReceiveAgree && agreeState.dormantDisableAgree) {
        agreeAll = true;
      }

      setAgreeState({
          ...agreeState,
          agreeAll: agreeAll,
          serviceTermAgree: !agreeState.serviceTermAgree
        })
    }
  }

  const toggleOver14 = () => {
    if (agreeState.over14Agree) {
      setAgreeState({
        ...agreeState,
        agreeAll: false,
        over14Agree: !agreeState.over14Agree
      })
    } else {
      let agreeAll = false;
      if (agreeState.serviceTermAgree && agreeState.personalInfoTermAgree && agreeState.eventReceiveAgree && agreeState.dormantDisableAgree) {
        agreeAll = true;
      }

      setAgreeState({
          ...agreeState,
          agreeAll: agreeAll,
          over14Agree : !agreeState.over14Agree
      })
    }
  }

  const togglePersonalInfo = () => {
    if (agreeState.personalInfoTermAgree) {
      setAgreeState({
        ...agreeState,
        agreeAll: false,
        personalInfoTermAgree: !agreeState.personalInfoTermAgree
      })
    } else {
      let agreeAll = false;
      if (agreeState.over14Agree && agreeState.serviceTermAgree && agreeState.eventReceiveAgree && agreeState.dormantDisableAgree) {
        agreeAll = true;
      }

      setAgreeState({
          ...agreeState,
          agreeAll: agreeAll,
          personalInfoTermAgree: !agreeState.personalInfoTermAgree
        })
    }
  }

  const toggleEvent = () => {
    if (agreeState.eventReceiveAgree) {
      setAgreeState({
        ...agreeState,
        agreeAll: false,
        eventReceiveAgree: !agreeState.eventReceiveAgree
      })
    } else {
      let agreeAll = false;
      if (agreeState.over14Agree && agreeState.serviceTermAgree && agreeState.personalInfoTermAgree && agreeState.dormantDisableAgree) {
        agreeAll = true;
      }

      setAgreeState({
          ...agreeState,
          agreeAll: agreeAll,
          eventReceiveAgree: !agreeState.eventReceiveAgree
        })
    }
  }

  const toggleDormant = () => {
    if (agreeState.dormantDisableAgree) {
      setAgreeState({
        ...agreeState,
        agreeAll: false,
        dormantDisableAgree: !agreeState.dormantDisableAgree
      })
    } else {
      let agreeAll = false;
      if (agreeState.over14Agree && agreeState.serviceTermAgree && agreeState.personalInfoTermAgree && agreeState.eventReceiveAgree) {
        agreeAll = true;
      }

      setAgreeState({
          ...agreeState,
          agreeAll: agreeAll,
          dormantDisableAgree: !agreeState.dormantDisableAgree
        })
    }
  }



  const onPressBack = () => {
    navigation.goBack();
  };

  const checkValidity = () => {
    let valid = true;

    if (desc.trim() === '') {
      valid = false;
    }

    if (!bgChecked && !peopleChecked && !officialChecked) {
      valid = false;
    }

    if (!Regex.numberRegex.test(peoplePrice) || !Regex.numberRegex.test(bgPrice) || !Regex.numberRegex.test(officialPrice)) {
      valid = false;
    }

    if (!agreeState.over14Agree || !agreeState.serviceTermAgree || !agreeState.personalInfoTermAgree) {
      valid = false;
    }

    if (!nicknameChecked) {
      valid = false;
    }

    return valid;
  }

  const onClickSave = () => {

    const dto = new Seller.CreateDto();

    const specialty: string[] = [];
    peopleChecked && specialty.push(Specialty.PEOPLE);
    bgChecked && specialty.push(Specialty.BACKGROUND);
    officialChecked && specialty.push(Specialty.OFFICIAL);

    dto.username = signUpRedux.username;
    dto.sellerEnabledYN = 'Y';
    dto.snsType = User.SnsType.KAKAO;
    dto.nickname = nickname;
    dto.description = desc;
    dto.specialty = specialty.join(',');
    dto.peoplePrice = peoplePrice;
    dto.backgroundPrice = bgPrice;
    dto.officialPrice = officialPrice;
    dto.workHourFromDt = `${fromDt.getHours()}${fromDt.getMinutes()}`
    dto.workHourToDt = `${toDt.getHours()}${toDt.getMinutes()}`

    console.log(dto);

    signUpMutation.mutate(dto);
  }

  return (
    <SafeAreaView>
      <View style={{
        paddingHorizontal: 20,
        paddingTop: 12
      }}>
        <MaterialCommunityIcons name={"arrow-left"} color={"black"} size={24} onPress={onPressBack} />
      </View>
      <ScrollView contentContainerStyle={{
        paddingHorizontal: 12,
        paddingTop: 12,
        paddingBottom: 50
      }}>
        <Text style={styles.sectionTitle}>작가명<Text style={styles.sectionTitleStar}>*</Text></Text>
        <View style={{
          flexDirection: 'row',
          alignItems: 'center',
          borderWidth: 1,
          borderColor: showNicknameError ? 'red' : 'black',
          borderRadius: 6,
          paddingHorizontal: 10,
        }}>
          <TextInput value={nickname} onChangeText={onChangeNickname} style={{
            paddingVertical: 10,
            fontSize: 14,
            borderRadius: 6,
            flex: 1,
          }} placeholder={"작가명을 입력해주세요."} />
          <TouchableOpacity style={{
            backgroundColor: nickname.trim() === '' ? '#d9d9d9' : Colors.PRIMARY,
            paddingVertical: 4,
            paddingHorizontal: 4,
            borderRadius: 4
          }} disabled={nickname.trim() === ''} onPress={onClickCheckNickname}>
            <Text style={{
              fontSize: 12,
              color: 'black'
            }}>중복확인</Text>
          </TouchableOpacity>
        </View>

        {
          nicknameChecked && (
            <Text style={{
              fontSize: 10,
              marginTop: 4,
              color: 'green'
            }}>사용할 수 있는 닉네임입니다.</Text>
          )
        }

        {
          showNicknameError && (
            <Text style={{
              fontSize: 10,
              marginTop: 4,
              color: 'red'
            }}>다른 닉네임을 사용해주세요.</Text>
          )
        }


        <Text style={{
          fontSize: 12,
          marginTop: 6
        }}>의뢰인과의 신뢰를 위해 닉네임 변경은 불가하니 신중하게 결정해주세요!</Text>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <Text style={styles.sectionTitle}>자기소개<Text style={styles.sectionTitleStar}>*</Text></Text>
        <TextInput
          multiline
          value={desc}
          onChangeText={setDesc}
          textAlignVertical={'top'}
          style={{
            borderWidth: 1,
            borderColor: "black",
            paddingHorizontal: 10,
            paddingVertical: 10,
            fontSize: 14,
            borderRadius: 6,
            height: 100
        }} placeholder={"자기소개를 입력해주세요."} />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <View>
          <View style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
            <Text style={{ ...styles.sectionTitle, marginBottom: 0 }}>작업 전문 분야<Text style={styles.sectionTitleStar}>*</Text></Text>
            <TouchableOpacity style={{
              marginLeft: 12,
              backgroundColor: '#d3d3d3',
              paddingVertical: 4,
              paddingHorizontal: 8,
              borderRadius: 8,
            }}>
              <Text style={{
                fontSize: 12
              }}>설명보기</Text>
            </TouchableOpacity>
          </View>
          <Text style={{
            color: Colors.GRAY_TEXT,
            fontSize: 12
          }}>(중복가능)</Text>


          <View style={{
            flexDirection: 'row',
            alignItems: 'center',
            marginTop: 12,
          }}>

            <AppCheckBox title={'인물'} checked={peopleChecked} onPress={() => setPeopleChecked(!peopleChecked)} containerStyle={{ marginBottom: 16 }} boxSize={24} iconSize={18}/>
            <AppCheckBox title={'배경'} checked={bgChecked} onPress={() => setBgChecked(!bgChecked)} containerStyle={{ marginBottom: 16 }} boxSize={24} iconSize={18}/>
            <AppCheckBox title={'증명사진'} checked={officialChecked} onPress={() => setOfficialChecked(!officialChecked)} containerStyle={{ marginBottom: 16 }} boxSize={24} iconSize={18}/>

          </View>

          <Text style={{...styles.sectionTitle, marginTop: 8}}>인물 최소 비용<Text style={styles.sectionTitleStar}>*</Text></Text>
          <TextInput value={peoplePrice} onChangeText={setPeoplePrice} style={styles.textInput} placeholder={"최소 금액을 입력해주세요."} />

          <Text style={{...styles.sectionTitle, marginTop: 8}}>배경 최소 비용<Text style={styles.sectionTitleStar}>*</Text></Text>
          <TextInput value={bgPrice} onChangeText={setBgPrice} style={styles.textInput} placeholder={"최소 금액을 입력해주세요."} />

          <Text style={{...styles.sectionTitle, marginTop: 8}}>증명 최소 비용<Text style={styles.sectionTitleStar}>*</Text></Text>
          <TextInput value={officialPrice} onChangeText={setOfficialPrice} style={styles.textInput} placeholder={"최소 금액을 입력해주세요."} />

        </View>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <Text style={styles.sectionTitle}>활동 시간<Text style={styles.sectionTitleStar}>*</Text></Text>

        <View style={{
          flexDirection: 'row',
        }}>
          <View style={{
            flex: 1,
            marginRight: 6
          }}>
            <View style={{
              flexDirection: 'row',
              alignItems: 'center',
              height: 35,
            }}>
              <MaterialCommunityIcons name={'calendar-month-outline'} size={20}/>
              <Text style={{
                marginLeft: 4
              }}>From</Text>
            </View>
            <TouchableOpacity onPress={() => setIsFromOpen(true)} style={{
              borderWidth: 1,
              borderColor: 'black',
              padding: 12,
              borderRadius: 8,
              alignItems: 'center'
            }}>
              <Text>{ fromDt.getHours() }시 { fromDt.getMinutes() }분</Text>
            </TouchableOpacity>

          </View>
          <View style={{
            flex: 1
          }}>
            <View style={{
              flexDirection: 'row',
              alignItems: 'center',
              height: 35
            }}>
              <MaterialCommunityIcons name={'calendar-month-outline'} size={20}/>
              <Text style={{
                marginLeft: 4
              }}>To</Text>
            </View>
            <TouchableOpacity onPress={() => setIsToOpen(true)} style={{
              borderWidth: 1,
              borderColor: 'black',
              padding: 12,
              borderRadius: 8,
              alignItems: 'center'
            }}>
              <Text>{ toDt.getHours() }시 { toDt.getMinutes() }분</Text>
            </TouchableOpacity>
          </View>
        </View>
        <Text style={{
          fontSize: 12,
          marginTop: 20
        }}>설정하신 시간동안 작가목록에 노출되며 의뢰 요청을 받을 수 있습니다</Text>
        <Text style={{
          fontSize: 12,
          marginTop: 4,
          color: '#959595'
        }}>{`*평균 응답시간이 길어지면 의뢰인들이 찾지 않을 수 있어요!!\n 꼭 활동 가능한 시간대로 설정해 주세요!`}</Text>



        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <View>
          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12,
            backgroundColor: '#f2f3f7',
            alignItems: 'center',
          }}>
            <Text style={{
              fontSize: 16,
              fontWeight: '600'
            }}>모두 동의합니다.</Text>
            <AppCheckBox checked={agreeState.agreeAll} onPress={toggleAgreeAll} shape={'circle'} containerStyle={{ backgroundColor: '#f2f3f7'}}  boxSize={26} iconSize={16}/>
          </View>
          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text>만 14세 이상입니다.<Text style={{ color: '#f18a7c', fontWeight: 'bold' }}> (필수)</Text></Text>
            <AppCheckBox checked={agreeState.over14Agree} onPress={toggleOver14} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text><Text onPress={() => {}} style={{
              textDecorationLine: 'underline'
            }}>서비스 이용약관</Text>에 동의합니다.<Text style={{ color: '#f18a7c', fontWeight: 'bold' }}> (필수)</Text></Text>
            <AppCheckBox checked={agreeState.serviceTermAgree} onPress={toggleServiceTerm} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text><Text onPress={() => {}} style={{
              textDecorationLine: 'underline'
            }}>개인정보 수집/이용</Text>에 동의합니다.<Text style={{ color: '#f18a7c', fontWeight: 'bold' }}> (필수)</Text></Text>
            <AppCheckBox checked={agreeState.personalInfoTermAgree} onPress={togglePersonalInfo} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text>이벤트, 할인 혜택 알림 수신에 동의합니다. (선택)</Text>
            <AppCheckBox checked={agreeState.eventReceiveAgree} onPress={toggleEvent} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text>장기 미접속 시, 계정을 활성 상태로 유지합니다. (선택)</Text>
            <AppCheckBox checked={agreeState.dormantDisableAgree} onPress={toggleDormant} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>
        </View>

        <AppButton onPress={onClickSave} title={'가입완료!'} disabled={!checkValidity()}/>

        <DatePicker
          modal
          open={isFromOpen}
          minuteInterval={10}
          date={fromDt}
          mode={'time'}
          maximumDate={toDt}
          onConfirm={(date) => {
            console.log(date);
            setIsFromOpen(false)
            setFromDt(date)
          }}
          onCancel={() => {
            setIsFromOpen(false)
          }}
        />

        <DatePicker
          modal
          open={isToOpen}
          minuteInterval={10}
          date={toDt}
          mode={'time'}
          minimumDate={fromDt}
          onConfirm={(date) => {
            console.log(date);
            setIsToOpen(false)
            setToDt(date)
          }}
          onCancel={() => {
            setIsToOpen(false)
          }}
        />
      </ScrollView>

    </SafeAreaView>
  );

};

const styles = StyleSheet.create({
  sectionTitle: {
    marginBottom: 8,
    fontSize: 16,
    color: 'black'
  },
  sectionTitleStar: {
    color: '#ea4c4c',
  },
  textInput: {
    borderWidth: 1,
    borderColor: 'black',
    paddingHorizontal: 10,
    paddingVertical: 10,
    fontSize: 14,
    borderRadius: 6
  },
  checkBoxText: {
    marginHorizontal: 8
  }
})

export default SignUpSellerScreen;
