import React from "react";
import { useNavigation } from "@react-navigation/native";
import { useAppDispatch } from "../../store/config";
import { setShowLoginGuideModal } from "../../store/slices/commonSlice";
import { Alert, SafeAreaView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Colors } from "../../colors";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import { RouteNames } from "../../AppNav";

const SignUpSelectTypeScreen = () => {
  const navigation = useNavigation();

  const onPressBack = () => {
    navigation.goBack();
  };

  const onPressClient = () => {
    navigation.navigate(RouteNames.SignUpClient)
  }

  const onPressSeller = () => {
    navigation.navigate(RouteNames.SignUpSeller)
  }

  return (
    <SafeAreaView style={{ flex: 1, justifyContent: "space-between", paddingBottom: 30 }}>
      <View style={{
        paddingHorizontal: 20,
        paddingTop: 12
      }}>
        <MaterialCommunityIcons name={"arrow-left"} color={"black"} size={24} onPress={onPressBack} />
      </View>

      <Text style={styles.message}>{`당신은\n누구십니까?`}</Text>

      <View>
        <TouchableOpacity style={styles.button} onPress={onPressClient}>
          <View style={styles.buttonTitleContainer}>
            <Text style={styles.buttonTitle}>의뢰인으로 가입</Text>
            <Text style={styles.buttonSubTitle}>
              나의 사진을 멋지게 바꿔줄 전문가를 찾는 당신!
            </Text>
          </View>
          <MaterialCommunityIcons name={"chevron-right"} size={30} color={"white"}/>

          <View />
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={onPressSeller}>
          <View style={styles.buttonTitleContainer}>
            <Text style={styles.buttonTitle}>전문가로 가입</Text>
            <Text style={styles.buttonSubTitle}>
              재능을 발휘하고 수익까지 창출하길 원하는 당신!
            </Text>
          </View>
          <MaterialCommunityIcons name={"chevron-right"} size={30} color={"white"}/>

          <View />
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
  button: {
    backgroundColor: "#D9D9D9",
    height: 65,
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 20,
    marginHorizontal: 12,
    marginBottom: 12,
    borderRadius: 4,
  },
  buttonTitleContainer: {
    flex: 1
  },
  buttonTitle: {
    color: "black",
    fontSize: 20,
  },
  buttonSubTitle: {
    color: Colors.GRAY_TEXT,
    fontSize: 14,
  },
});

export default SignUpSelectTypeScreen;
