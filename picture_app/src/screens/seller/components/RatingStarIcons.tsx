import React, { FC } from "react";
import Icon from "react-native-vector-icons/Ionicons";
import { Colors } from "../../../colors";

type RatingStarIconsProps = {
  rateAvg: number
}
const RatingStarIcons: FC<RatingStarIconsProps> = ({ rateAvg }) => {
  return (
    <>
      {
        [...Array(Math.floor(rateAvg))].map((_, i) => {
          return (
            <Icon key={i} name={"ios-star"} size={14} color={Colors.PRIMARY} style={{
              paddingLeft: 2
            }}/>
          )
        })
      }
      {
        [...Array(5 - Math.floor(rateAvg))].map((_, i) => {
          return (
            <Icon key={i} name={"ios-star"} size={14} color={Colors.GRAY_TEXT} style={{
              paddingLeft: 2
            }}/>
          )
        })
      }
    </>
  )
}

export default RatingStarIcons;
