import React, { FC } from "react";
import { StyleSheet, Text, TouchableOpacity } from "react-native";
import { Colors } from "../colors";

type AppButtonProps = {
  title: string,
  disabled?: boolean,
  onPress: () => void,
}

const AppButton: FC<AppButtonProps> = ({ title, disabled, onPress }) => {
  return (
    <TouchableOpacity disabled={disabled} onPress={onPress} style={disabled ? styles.disabledContainer : styles.container}>
      <Text style={disabled ? styles.disabledText : styles.text}>{title}</Text>
    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: Colors.PRIMARY,
    height: 50,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 20
  },
  text: {
    fontSize: 16,
    color: 'black',
  },
  disabledContainer: {
    backgroundColor: '#d9d9d9',
    height: 50,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    marginTop: 20
  },
  disabledText: {
    fontSize: 16,
    color: Colors.GRAY_TEXT
  }
})

export default AppButton;
