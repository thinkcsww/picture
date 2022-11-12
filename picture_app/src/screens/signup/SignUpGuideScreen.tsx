import React from "react";
import { Platform, SafeAreaView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useNavigation } from "@react-navigation/native";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../colors";
import { useMutation } from "react-query";
import { AuthService } from "../../services/AuthService";
import { AxiosError } from "axios";
import { getProfile, KakaoOAuthToken, KakaoProfile, login } from "@react-native-seoul/kakao-login";
import { Auth } from "../../types/Auth";
import { RouteNames } from "../../AppNav";
import { useAppDispatch, useAppSelector } from "../../store/config";
import { setSignUpRedux } from "../../store/slices/signUpSlice";
import AsyncStorageService from "../../services/AsyncStorageService";
import UserService from "../../services/UserService";
import { setUser } from "../../store/slices/commonSlice";

const SignUpGuideScreen = () => {

  const navigation = useNavigation<any>();
  const dispatch = useAppDispatch();
  const { signUpRedux } = useAppSelector(state => state.signUp);

  const loginMutation = useMutation(AuthService.QueryKey.login, (dto: Auth.LoginDto) => {
    dispatch(setSignUpRedux({ username: dto.username, token: dto.token }))
    return AuthService.login(dto);
  }, {
    onSuccess: async (result: Auth.MyOAuth2Token) => {
      console.log('==== 로그인 성공 ====');
      console.log(result);

      AuthService.setTokenInfo(result).then();
      const userMeResponse =  await UserService.getUserMe()
      dispatch(setUser(userMeResponse));

      navigation.navigate(signUpRedux.destination.key, {...signUpRedux.destination.params})
    },
    onError: (error: AxiosError<any>) => {
      if (error.response && error.response.data.message.includes('not found')) {
        console.log('==== 회원가입 시작 ====');
        navigation.navigate(RouteNames.SignUpSelectType)

      } else {
        console.log('==== 로그인 실패 ====');
      }

    }
  })

  const onPressKakao = async () => {
    const token: KakaoOAuthToken = await login();
    const profile: KakaoProfile = await getProfile() as KakaoProfile;
    console.log('==== 카카오 토큰 ====');
    console.log(token);
    console.log(token.accessToken)
    console.log('==== 카카오 프로필 ====');
    console.log(profile);

    const dto = new Auth.LoginDto(profile.id, token.accessToken);

    loginMutation.mutate(dto);

  };

  const onPressBack = () => {
    navigation.goBack();
  };

  return (
    <SafeAreaView style={{ flex: 1, justifyContent: "space-between", paddingBottom: 30 }}>
      <View />

      <Text style={styles.message}>{`안녕하세요!\n자유로운 포토라이프를 위해\n로그인이 필요해요!`}</Text>

      <View>
        <TouchableOpacity style={styles.kakaoButton} onPress={onPressKakao}>
          <Icon name={"chatbubble-sharp"} size={20} color={"black"} />
          <Text style={styles.kakaoButtonText}>카카오톡으로 시작</Text>
          <View />
        </TouchableOpacity>
        {
          Platform.OS === "ios" && (
            <TouchableOpacity style={styles.appleButton}>
              <Icon name={"logo-apple"} size={20} color={"white"} />
              <Text style={styles.appleButtonText}>Apple로 시작</Text>
              <View />
            </TouchableOpacity>
          )
        }

        <TouchableOpacity style={styles.goBackButton} onPress={onPressBack}>
          <Text style={styles.goBackButtonText}>
            {`로그인 전 돌아보기 \>`}
          </Text>
        </TouchableOpacity>
      </View>

    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  message: {
    alignSelf: "center",
    fontSize: 30,
    textAlign: "center",
    color: "black",
  },
  kakaoButton: {
    backgroundColor: "#f7e54b",
    height: 50,
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 20,
    marginHorizontal: 12,
    justifyContent: "space-between",
    marginBottom: 12,
    borderRadius: 4,
  },
  appleButton: {
    backgroundColor: "black",
    height: 50,
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 20,
    borderRadius: 4,
    marginHorizontal: 12,
    justifyContent: "space-between",
  },
  kakaoButtonText: {
    fontSize: 16,
    fontWeight: "600",
    color: "black",
  },
  appleButtonText: {
    fontSize: 16,
    fontWeight: "600",
    color: "white",
  },
  goBackButton: {
    alignItems: "center",
    marginTop: 12,
  },
  goBackButtonText: {
    color: Colors.GRAY_TEXT,
    fontWeight: "700",
  },
});

export default SignUpGuideScreen;
