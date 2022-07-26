import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import AbstractHoc from "./AbstractHoc";
import AppNav from "./AppNav";

const AppNavContainer = () => {

  return (
    <NavigationContainer>
      <AbstractHoc>
        <AppNav/>
      </AbstractHoc>
    </NavigationContainer>
  );
};

export default AppNavContainer;
