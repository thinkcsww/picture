import { Image, View } from "react-native";
import React, { FC, useState } from "react";
import { Env } from "../constants/Env";
import Images from "../../assets/images";

type ImageWithPHProps = {
  styles: any,
  fileName?: string
}
const ImageWithPH: FC<ImageWithPHProps> = ({ styles, fileName }) => {
  return (
    <Image
      source={{ uri: fileName ? `${Env.host}/api/v1/files/images/${fileName}` : `` }}
      defaultSource={fileName ? Images.grayDefault : Images.profile.dummy}
      style={{...styles}}
    />
  );
};

export default ImageWithPH;
