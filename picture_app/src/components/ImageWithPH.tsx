import { Image, View } from "react-native";
import React, { useState } from "react";
import { Env } from "../constants/Env";
import Images from "../../assets/images";

const ImageWithPH = ({ styles, fileName }: any) => {
  return (
    <Image
      source={{ uri: fileName ? `${Env.host}/api/v1/files/images/${fileName}` : `` }}
      defaultSource={fileName ? Images.grayDefault : Images.profile.dummy}
      style={{...styles}}
    />
  );
};

export default ImageWithPH;
