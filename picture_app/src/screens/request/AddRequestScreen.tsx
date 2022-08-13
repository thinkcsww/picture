import React, { useState } from "react";
import { Alert, SafeAreaView, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import AppCheckBox from "../../components/AppCheckBox";
import { Specialty } from "../../types/Common";
import AppHeader from "../../components/AppHeader";
import { Divider } from "react-native-paper";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import DatePicker from "react-native-date-picker";
import DateUtils from "../../utils/DateUtils";
import { useMutation, useQueryClient } from "react-query";
import { RequestService } from "../../services/RequestService";
import { Request } from "../../types/Request";
import { AxiosError } from "axios";
import { useNavigation } from "@react-navigation/native";
import { Regex } from "../../utils/Regex";
import { Colors } from "../../colors";

const AddRequestScreen = () => {

  const [selectedSpecialty, setSelectedSpecialty] = useState<Specialty>(Specialty.PEOPLE);
  const [title, setTitle] = useState('');
  const [desiredPrice, setDesiredPrice] = useState('');
  const [dueDt, setDueDt] = useState(new Date())
  const [desc, setDesc] = useState('');
  const [isDatePickerOpen, setIsDatePickerOpen] = useState(false)
  const navigation = useNavigation();
  const queryClient = useQueryClient();

  const createRequestMutation = useMutation(RequestService.QueryKey.createRequest, (dto: Request.CreateDto) => {
    return RequestService.createRequest(dto);
  }, {
    onSuccess: (result) => {
      console.log('==== 공개의뢰 생성 성공 ====');
      console.log(result);

      queryClient.invalidateQueries(RequestService.QueryKey.getRequests).then();
      navigation.goBack();
    },
    onError: (e: AxiosError) => {
      console.log('==== 공개의뢰 생성 실패 ====');
      console.log(e.message);
    }
  });

  const checkValidity = () => {
    if (title.trim() === '') {
      return false;
    }

    if (!Regex.numberRegex.test(desiredPrice)) {
      return false;
    }

    if (desc.trim() === '') {
      return false;
    }

    return true;
  }

  const onClickSave = () => {
    if (!checkValidity()) {
      Alert.alert('올바른 값을 입력해주세요');
      return;
    }

    const dto = new Request.CreateDto();
    dto.specialty = selectedSpecialty;
    dto.title = title;
    dto.desiredPrice = +desiredPrice;
    dto.dueDate = dueDt.toISOString();
    dto.description = desc;
    dto.matchYn = 'N';
    dto.completeYn = 'N';
    createRequestMutation.mutate(dto);
  }


  return (
    <ScrollView>
      <SafeAreaView>
        <AppHeader title={'공개 의뢰하기'} iconName={"close"} rightButton={'완료'} rightButtonStyle={{color: '#f1dd09', fontWeight: 'bold'}} rightButtonCallback={onClickSave}/>

        <View style={{
          paddingHorizontal: 12
        }}>
          <View style={{
            flexDirection: 'row',
            alignItems: 'center',
            marginTop: 12,
          }}>
            <AppCheckBox title={'인물'} checked={selectedSpecialty === Specialty.PEOPLE} onPress={() => setSelectedSpecialty(Specialty.PEOPLE)} containerStyle={{ marginBottom: 2 }} boxSize={24} iconSize={18} shape={'circle'}/>
            <AppCheckBox title={'배경'} checked={selectedSpecialty === Specialty.BACKGROUND} onPress={() => setSelectedSpecialty(Specialty.BACKGROUND)} containerStyle={{ marginBottom: 2 }} boxSize={24} iconSize={18} shape={'circle'}/>
            <AppCheckBox title={'증명사진'} checked={selectedSpecialty === Specialty.OFFICIAL} onPress={() => setSelectedSpecialty(Specialty.OFFICIAL)} containerStyle={{ marginBottom: 2 }} boxSize={24} iconSize={18} shape={'circle'}/>
            <AppCheckBox title={'기타'} checked={selectedSpecialty === Specialty.ETC} onPress={() => setSelectedSpecialty(Specialty.ETC)} containerStyle={{ marginBottom: 2 }} boxSize={24} iconSize={18} shape={'circle'}/>
          </View>

          <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 15 }} />
          <TextInput onChangeText={setTitle} placeholder={'제목'} placeholderTextColor={'#AAAAAA'} style={styles.textInput}/>
          <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 15 }} />
          <TextInput onChangeText={setDesiredPrice} placeholder={'₩ 희망 의뢰비 (예산)'} placeholderTextColor={'#AAAAAA'} style={styles.textInput}/>
          <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 15 }} />
          <View style={styles.dueDtContainer}>
            <Text style={styles.dueDtText}>마감기한        </Text>
            <TouchableOpacity onPress={() => setIsDatePickerOpen(true)} style={styles.dueDtInnerContainer}>
              <Text style={styles.dueDtText}>{DateUtils.getFormattedDate(dueDt.toISOString())}</Text>
              <MaterialCommunityIcons name={'chevron-down'} size={30}/>
            </TouchableOpacity>
          </View>
          <Divider style={{ height: 1, backgroundColor: "#aaaaaa", marginVertical: 25 }} />
          <View style={styles.descTextInputContainer}>
            <TextInput
              style={styles.descTextInput}
              multiline
              value={desc}
              onChangeText={setDesc}
              textAlignVertical={'top'}
              placeholderTextColor={'#AAAAAA'}
              placeholder={'요청 사항을 상세히 입력해주세요.\n개인정보 보호를 위해\n사진은 작가와 매칭되면 직접 전달하실 수 있습니다'}
            />
          </View>

        </View>
      </SafeAreaView>

      <DatePicker
        modal
        open={isDatePickerOpen}
        minuteInterval={10}
        date={dueDt}
        onConfirm={(date) => {
          console.log(date);
          setIsDatePickerOpen(false)
          setDueDt(date)
        }}
        onCancel={() => {
          setIsDatePickerOpen(false)
        }}
      />
    </ScrollView>
  )
}

const styles = StyleSheet.create({
  textInput: {
    fontSize: 17,
  },
  dueDtContainer: {
    flexDirection: 'row',
    marginTop: 10,
    paddingHorizontal: 4,
    alignItems: 'center'
  },
  dueDtInnerContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center'
  },
  dueDtText: {
    fontSize: 17,
    fontWeight: '400'
  },
  descTextInputContainer: {
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 6,
    backgroundColor: '#e9e9e9',
    height: 130,
  },
  descTextInput: {
    lineHeight: 28,
    fontSize: 16
  }
})

export default AddRequestScreen;
