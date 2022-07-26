import { Modal, Platform, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import React from "react";
import { SelectValue } from "../../../types/SelectValue";
import { Colors } from "../../../colors";
import { Specialty } from "../../../types/Common";

type TabListSpecialtySelectModal = {
  close: () => void,
  selectedSpecialty: Specialty,
  onSelect: (specialty: SelectValue<Specialty>) => void,
}

const TabListSpecialtySelectModal: React.FC<TabListSpecialtySelectModal> = ({close, selectedSpecialty, onSelect}) => {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const specialtyList = [
    new SelectValue('인물 사진', Specialty.PEOPLE),
    new SelectValue('배경 사진', Specialty.BACKGROUND),
    new SelectValue('증명 사진', Specialty.OFFICIAL),
  ]

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Functions
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  return (
    <Modal onRequestClose={close} transparent={true} animationType={"fade"} >
      <TouchableOpacity style={styles.container} onPress={close}>
        <View style={styles.selectBox}>
          {
            specialtyList.map((specialty) => {
              return (
                <TouchableOpacity key={specialty.value} onPress={() => onSelect(specialty)} style={styles.selectBoxItem}>
                  <Text style={specialty.value === selectedSpecialty ? styles.selectBoxItemSelectedText : styles.selectBoxItemNotSelectedText}>{specialty.label}</Text>
                </TouchableOpacity>
              )
            })
          }
        </View>
      </TouchableOpacity>
    </Modal>
  )
};

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
| Styles
|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#C4C4C480',
  },
  selectBox: {
    width: 200,
    backgroundColor: '#ffffff',
    top: Platform.OS === 'ios' ? 100 : 55,
    left: 10,
    borderRadius: 10,
    paddingVertical: 18,
    paddingHorizontal: 14
  },
  selectBoxItem: {
    height: 35,
    paddingVertical: 4
  },
  selectBoxItemSelectedText: {
    fontWeight: 'bold',
    fontSize: 16,
    color: 'black'
  },
  selectBoxItemNotSelectedText: {
    fontSize: 16,
    color: Colors.GRAY_TEXT
  },

});

export default TabListSpecialtySelectModal;
