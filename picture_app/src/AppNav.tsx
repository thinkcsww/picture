import React from "react";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import RequestScreen from "./screens/request/RequestScreen";
import ChattingScreen from "./screens/chatting/ChattingScreen";
import MyPageScreen from "./screens/mypage/MyPageScreen";
import { NavigationContainer } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";
import SellerScreen from "./screens/seller/SellerScreen";
import SellerDetailScreen from "./screens/seller/SellerDetailScreen";
import { createMaterialBottomTabNavigator } from "@react-navigation/material-bottom-tabs";

const AppNav = () => {

  const Stack = createStackNavigator();

  const SellerStack = ({ navigation }) => {
    return (
      <Stack.Navigator screenOptions={{
        headerLeft: () => <MaterialCommunityIcons name={"arrow-left-bold"} size={26} onPress={() => navigation.goBack()} style={{ paddingLeft: 10 }}/>,
        headerTitleAlign: 'center'
      }}>
        <Stack.Screen name={"Seller"} component={SellerScreen} options={{ headerShown: false }}/>
        <Stack.Screen name={"SellerDetail"} component={SellerDetailScreen}/>
      </Stack.Navigator>
    )
  }



  const Tab = createMaterialBottomTabNavigator();

  return (
    <NavigationContainer>
      <Tab.Navigator
        shifting={false}
        barStyle={{ backgroundColor: "#ffffff" }}
        activeColor={"#f6ec78"}
        initialRouteName="작가">
        <Tab.Screen
          name="작가"
          component={SellerStack}
          options={{
            tabBarLabel: "홈",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="home" color={color} size={26} />
            ),
          }}
        />
        <Tab.Screen
          name="의뢰"
          component={RequestScreen}
          options={{
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="human-handsup" color={color} size={26} />
            ),
          }}
        />
        <Tab.Screen
          name="의뢰하기"
          component={RequestScreen}
          options={{
            tabBarLabel: "의뢰하기",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="book-edit-outline" color={color} size={26} />
            ),
          }}
        />
        <Tab.Screen
          name="채팅"
          component={ChattingScreen}
          options={{
            tabBarLabel: "채팅",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="chat" color={color} size={26} />
            ),
            tabBarBadge: 5

          }}/>
        <Tab.Screen
          name="마이페이지"
          component={MyPageScreen}
          options={{
            tabBarLabel: "마이페이지",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="face-man-outline" color={color} size={26} />
            ),
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  )
}

export default AppNav;
