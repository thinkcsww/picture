import React from "react";
import MaterialCommunityIcons from "react-native-vector-icons/MaterialCommunityIcons";
import RequestScreen from "./screens/request/RequestScreen";
import ChattingScreen from "./screens/chatting/ChattingScreen";
import MyPageScreen from "./screens/mypage/MyPageScreen";
import { useNavigation } from "@react-navigation/native";
import { createStackNavigator, TransitionPresets } from "@react-navigation/stack";
import SellerScreen from "./screens/seller/SellerScreen";
import SellerDetailScreen from "./screens/seller/SellerDetailScreen";
import { createMaterialBottomTabNavigator } from "@react-navigation/material-bottom-tabs";
import { useAppSelector } from "./store/config";
import SignUpSelectTypeScreen from "./screens/signup/SignUpSelectTypeScreen";
import SignUpGuideScreen from "./screens/signup/SignUpGuideScreen";
import SignUpSellerScreen from "./screens/signup/SignUpSellerScreen";
import SignUpClientScreen from "./screens/signup/SignUpClientScreen";
import AddRequestScreen from "./screens/request/AddRequestScreen";
import RequestDetailScreen from "./screens/request/RequestDetailScreen";

export const RouteNames = {
  SignUpClient: "SignUpClient",
  SignUpSeller: "SignUpSeller",
  SignUpGuide: "SignUpGuide",
  SignUpSelectType: "SignUpSelectType",

  SellerTab: "SellerTab",
  Seller: "Seller",
  SellerDetail: "SellerDetail",

  RequestTab: "RequestTab",
  Request: "Request",
  RequestDetail: "RequestDetail",
  AddRequest: "AddRequest",

  ChattingTab: "ChattingTab",
  Chatting: "Chatting",

  MyPageTab: "MyPageTab",
  MyPage: "MyPage",
};

const AppNav = () => {

  const { user } = useAppSelector(state => state.common);
  const navigation = useNavigation<any>();
  const Stack = createStackNavigator();

  const SellerStack = () => {
    return (
      <Stack.Navigator screenOptions={{
        headerTitleAlign: "center",
        cardStyle: { backgroundColor: '#fff' }
      }}>
        <Stack.Screen name={RouteNames.Seller} component={SellerScreen} options={{ headerShown: false }} />
      </Stack.Navigator>
    );
  };

  const RequestStack = () => {
    return (
      <Stack.Navigator screenOptions={{
        headerTitleAlign: "center",
        cardStyle: { backgroundColor: '#fff' }
      }}>
        <Stack.Screen name={RouteNames.Request} component={RequestScreen} options={{ headerShown: false }} />
      </Stack.Navigator>
    )
  }

  const ChattingStack = () => {
    return (
      <Stack.Navigator screenOptions={{
        headerTitleAlign: "center",
        cardStyle: { backgroundColor: '#fff' }
      }}>
        <Stack.Screen name={RouteNames.Chatting} component={ChattingScreen} options={{ headerShown: false }} />
      </Stack.Navigator>
    )
  }

  const MyPageStack = () => {
    return (
      <Stack.Navigator screenOptions={{
        headerTitleAlign: "center",
        cardStyle: { backgroundColor: '#fff' }
      }}>
        <Stack.Screen name={RouteNames.MyPage} component={MyPageScreen} options={{ headerShown: false }} />
      </Stack.Navigator>
    )
  }


  const Tab = createMaterialBottomTabNavigator();

  const HomeTabs = () => {
    return (
      <Tab.Navigator
        shifting={false}
        barStyle={{ backgroundColor: "#ffffff" }}
        activeColor={"#f6ec78"}
        initialRouteName={RouteNames.Seller}>
        <Tab.Screen
          name={RouteNames.SellerTab}
          component={SellerStack}
          options={{
            tabBarLabel: "???",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="home" color={color} size={26} />
            ),
          }}
        />
        <Tab.Screen
          name={RouteNames.RequestTab}
          component={RequestStack}
          options={{
            tabBarLabel: "??????",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="human-handsup" color={color} size={26} />
            ),
          }}
        />
        <Tab.Screen
          name={RouteNames.AddRequest}
          component={AddRequestScreen}
          // listeners={tabEventListenerShowLoginScreen}
          options={{
            tabBarLabel: "????????????",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="book-edit-outline" color={color} size={26} />
            ),
          }}
        />
        <Tab.Screen
          name={RouteNames.ChattingTab}
          component={ChattingStack}
          listeners={tabEventListenerShowLoginScreen}
          options={{
            tabBarLabel: "??????",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="chat" color={color} size={26} />
            ),
            tabBarBadge: 5,
          }} />
        <Tab.Screen
          name={RouteNames.MyPageTab}
          component={MyPageStack}
          options={{
            tabBarLabel: "???????????????",
            tabBarIcon: ({ color }) => (
              <MaterialCommunityIcons name="face-man-outline" color={color} size={26} />
            ),
          }}
        />
      </Tab.Navigator>
    );
  };

  const tabEventListenerShowLoginScreen = {
    tabPress: (e: any) => {
      if (user === undefined) {
        navigation.navigate(RouteNames.SignUpGuide)
        e.preventDefault();
        return;
      }
    },
  }

  return (
    <Stack.Navigator initialRouteName={"Tabs"} screenOptions={{ headerShown: false, cardStyle: { backgroundColor: '#fff' } }}>
      <Stack.Screen name="Tabs" component={HomeTabs} options={{ headerShown: false }} />
      <Stack.Screen name={RouteNames.SellerDetail} component={SellerDetailScreen} options={{ headerShown: false }} />
      <Stack.Screen name={RouteNames.RequestDetail} component={RequestDetailScreen} options={{ headerShown: false }} />

      <Stack.Group>
        <Stack.Screen name={RouteNames.SignUpGuide} component={SignUpGuideScreen} options={{ headerShown: false, ...TransitionPresets.ModalSlideFromBottomIOS}} />
        <Stack.Screen name={RouteNames.SignUpSelectType} component={SignUpSelectTypeScreen} options={{ headerShown: false }} />
        <Stack.Screen name={RouteNames.SignUpSeller} component={SignUpSellerScreen} options={{ headerShown: false }}/>
        <Stack.Screen name={RouteNames.SignUpClient} component={SignUpClientScreen} options={{ headerShown: false }}/>
      </Stack.Group>

    </Stack.Navigator>
  );
};

export default AppNav;
