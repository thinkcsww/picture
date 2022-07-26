import React, { FC } from "react";
import { Image, StyleSheet, View } from "react-native";
import { CheckBox } from "@rneui/base";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../colors";

type AppCheckBoxProps = {
  title?: string,
  checked: boolean,
  onPress: () => void,
  containerStyle?: {},
  shape?: 'circle' | 'square',
  boxSize: number,
  iconSize: number
}
const AppCheckBox: FC<AppCheckBoxProps> = ({ title, checked, onPress, containerStyle, shape, boxSize, iconSize }) => {

  const containerStyles = [
    styles.defaultContainerStyle, containerStyle
  ];

  let textStyles = checked ?
    styles.checked : styles.unChecked;

  // if( textSmall ){
  //   textStyles = textSmall ?
  //     styles.smallChecked : styles.smallUnChecked
  // }
  return (
    <CheckBox
      title={title}
      containerStyle={containerStyles}
      textStyle={{
        margin: 0,
        fontWeight: 'normal'
      }}
      checkedIcon={
        <View style={{ ...styles.checked, borderRadius: shape === 'circle' ? boxSize / 2 : 0, width: boxSize, height: boxSize}}>
          <Icon name={"checkmark"} size={iconSize} color={'white'} />
        </View>
      }
      uncheckedIcon={
        <View style={{ ...styles.unChecked, borderRadius: shape === 'circle' ? boxSize / 2 : 0, width: boxSize, height: boxSize}}>
          <Icon name={"checkmark"} size={iconSize} color={'#e4e4e4'} />
        </View>}
      checked={checked}
      onPress={onPress}
    />
  );
};

const styles = StyleSheet.create({

  defaultContainerStyle: {
    padding: 0,
    margin: 0,
  },
  checked: {
    justifyContent: "center",
    alignItems: "center",
    width: 30,
    height: 30,
    borderRadius: 15,
    backgroundColor: Colors.PRIMARY,
  },
  unChecked: {
    justifyContent: "center",
    alignItems: "center",
    width: 30,
    height: 30,
    borderRadius: 15,
    borderColor: '#e4e4e4',
    borderWidth: 2,
  },
  smallChecked: {
    color: "#838383",
    fontSize: 14,
  },
  smallUnChecked: {
    color: "#838383",
    fontSize: 14,
  },
});

export default AppCheckBox;
