import React, { useState } from "react";
import { SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { Divider } from "react-native-paper";
import { Colors } from "../../colors";
import AppCheckBox from "../../components/AppCheckBox";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import DatePicker from "react-native-date-picker";
import { Seller } from "../../types/Seller";
import { Specialty } from "../../types/Common";

const SignUpSellerScreen = () => {

  const [showError, setShowError] = useState(false);

  const [nickname, setNickname] = useState('')
  const [desc, setDesc] = useState('')
  const [peopleChecked, setPeopleChecked] = useState(false)
  const [bgChecked, setBgChecked] = useState(false)
  const [officialChecked, setOfficialChecked] = useState(false)
  const [peoplePrice, setPeoplePrice] = useState('')
  const [bgPrice, setBgPrice] = useState('')
  const [officialPrice, setOfficialPrice] = useState('')
  const [agreeAll, setAgreeAll] = useState(false);

  const [over14Agree, setOver14Agree] = useState(false);
  const [serviceTermAgree, setServiceTermAgree] = useState(false);
  const [personalInfoTermAgree, setPersonalInfoTermAgree] = useState(false);
  const [eventReceiveAgree, setEventReceiveAgree] = useState(false);
  const [dormantDisableAgree, setDormantDisableAgree] = useState(false);

  const [fromDt, setFromDt] = useState(new Date())
  const [isFromOpen, setIsFromOpen] = useState(false)

  const [toDt, setToDt] = useState(new Date())
  const [isToOpen, setIsToOpen] = useState(false)

  const checkValidity = () => {
    return true;
  }

  const onClickSave = () => {
    if (!checkValidity()) {
      setShowError(true);
      return;
    }



    const dto = new Seller.CreateDto();

    const specialty: string[] = [];
    peopleChecked && specialty.push(Specialty.PEOPLE);
    bgChecked && specialty.push(Specialty.BACKGROUND);
    officialChecked && specialty.push(Specialty.OFFICIAL);

    dto.snsType = 'KAKAO';
    dto.nickname = nickname;
    dto.description = desc;
    dto.specialty = specialty.join(',');
    dto.peoplePrice = peoplePrice;
    dto.backgroundPrice = bgPrice;
    dto.officialPrice = officialPrice;
    dto.workHourFromDt = `${fromDt.getHours()}${fromDt.getMinutes()}`
    dto.workHourToDt = `${toDt.getHours()}${toDt.getMinutes()}`

  }

  return (
    <SafeAreaView>
      <ScrollView contentContainerStyle={{
        paddingHorizontal: 12,
        paddingTop: 12
      }}>
        <View>

        </View>

        <Text style={styles.sectionTitle}>?????????<Text style={styles.sectionTitleStar}>*</Text></Text>
        <TextInput value={nickname} onChangeText={setNickname} style={styles.textInput} placeholder={"???????????? ??????????????????."} />
        <Text style={{
          fontSize: 10,
          marginTop: 6
        }}>??????????????? ????????? ?????? ????????? ????????? ???????????? ???????????? ??????????????????!</Text>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <Text style={styles.sectionTitle}>????????????<Text style={styles.sectionTitleStar}>*</Text></Text>
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
        }} placeholder={"??????????????? ??????????????????."} />

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <View>
          <View style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
            <Text style={{ ...styles.sectionTitle, marginBottom: 0 }}>?????? ?????? ??????</Text>
            <TouchableOpacity style={{
              marginLeft: 12,
              backgroundColor: '#d3d3d3',
              paddingVertical: 4,
              paddingHorizontal: 8,
              borderRadius: 8,
            }}>
              <Text style={{
                fontSize: 12
              }}>????????????</Text>
            </TouchableOpacity>
          </View>
          <Text style={{
            color: Colors.GRAY_TEXT,
            fontSize: 12
          }}>(????????????)</Text>


          <View style={{
            flexDirection: 'row',
            alignItems: 'center',
            marginTop: 12,
          }}>

            <AppCheckBox title={'??????'} checked={peopleChecked} onPress={() => setPeopleChecked(!peopleChecked)} containerStyle={{ marginBottom: 16 }} boxSize={24} iconSize={18}/>
            <AppCheckBox title={'??????'} checked={bgChecked} onPress={() => setBgChecked(!bgChecked)} containerStyle={{ marginBottom: 16 }} boxSize={24} iconSize={18}/>
            <AppCheckBox title={'????????????'} checked={officialChecked} onPress={() => setOfficialChecked(!officialChecked)} containerStyle={{ marginBottom: 16 }} boxSize={24} iconSize={18}/>

          </View>

          <Text style={{...styles.sectionTitle, marginTop: 8}}>?????? ?????? ??????<Text style={styles.sectionTitleStar}>*</Text></Text>
          <TextInput value={peoplePrice} onChangeText={setPeoplePrice} style={styles.textInput} placeholder={"?????? ????????? ??????????????????."} />

          <Text style={{...styles.sectionTitle, marginTop: 8}}>?????? ?????? ??????<Text style={styles.sectionTitleStar}>*</Text></Text>
          <TextInput value={bgPrice} onChangeText={setBgPrice} style={styles.textInput} placeholder={"?????? ????????? ??????????????????."} />

          <Text style={{...styles.sectionTitle, marginTop: 8}}>?????? ?????? ??????<Text style={styles.sectionTitleStar}>*</Text></Text>
          <TextInput value={officialPrice} onChangeText={setOfficialPrice} style={styles.textInput} placeholder={"?????? ????????? ??????????????????."} />

        </View>

        <Divider style={{ height: 1, marginVertical: 20, marginHorizontal: 12 }} />

        <Text style={styles.sectionTitle}>?????? ??????<Text style={styles.sectionTitleStar}>*</Text></Text>

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
              height: 35
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
              <Text>{ fromDt.getHours() }??? { fromDt.getMinutes() }???</Text>
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
              <Text>{ toDt.getHours() }??? { toDt.getMinutes() }???</Text>
            </TouchableOpacity>
          </View>
        </View>



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
            }}>?????? ???????????????.</Text>
            <AppCheckBox checked={agreeAll ? agreeAll : over14Agree && serviceTermAgree && personalInfoTermAgree && eventReceiveAgree && dormantDisableAgree} onPress={() => setAgreeAll(!agreeAll)} shape={'circle'} containerStyle={{ backgroundColor: '#f2f3f7'}}  boxSize={26} iconSize={16}/>
          </View>
          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text>??? 14??? ???????????????.<Text style={{ color: '#f18a7c', fontWeight: 'bold' }}> (??????)</Text></Text>
            <AppCheckBox checked={agreeAll ? true : over14Agree} onPress={() => setOver14Agree(!over14Agree)} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text><Text onPress={() => {}} style={{
              textDecorationLine: 'underline'
            }}>????????? ????????????</Text>??? ???????????????.<Text style={{ color: '#f18a7c', fontWeight: 'bold' }}> (??????)</Text></Text>
            <AppCheckBox checked={agreeAll ? true : serviceTermAgree} onPress={() => setServiceTermAgree(!serviceTermAgree)} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text><Text onPress={() => {}} style={{
              textDecorationLine: 'underline'
            }}>???????????? ??????/??????</Text>??? ???????????????.<Text style={{ color: '#f18a7c', fontWeight: 'bold' }}> (??????)</Text></Text>
            <AppCheckBox checked={agreeAll ? true : personalInfoTermAgree} onPress={() => setPersonalInfoTermAgree(!personalInfoTermAgree)} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text>?????????, ?????? ?????? ?????? ????????? ???????????????. (??????)</Text>
            <AppCheckBox checked={agreeAll ? true : eventReceiveAgree} onPress={() => setEventReceiveAgree(!eventReceiveAgree)} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>

          <View style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
            padding: 12
          }}>
            <Text>?????? ????????? ???, ????????? ?????? ????????? ???????????????. (??????)</Text>
            <AppCheckBox checked={agreeAll ? true : dormantDisableAgree} onPress={() => setDormantDisableAgree(!dormantDisableAgree)} shape={'circle'}  boxSize={24} iconSize={16}/>
          </View>
        </View>

        <DatePicker
          modal
          open={isFromOpen}
          minuteInterval={10}
          date={fromDt}
          mode={'time'}
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
