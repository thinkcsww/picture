import React from "react";
import { Modal, Platform, SafeAreaView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useNavigation } from "@react-navigation/native";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../colors";
import { RouteNames } from "../../AppNav";

const SignUpGuideScreen = () => {

  const navigation = useNavigation();

  const onPressKakao = () => {
    navigation.navigate(RouteNames.SignUpSelectType);
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
