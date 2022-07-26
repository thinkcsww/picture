import React, { FC, useEffect } from "react";
import { useAxiosLoader } from "./hooks/useAxiosLoader";

const AbstractHoc: FC = ({children}) => {

  const [loading, ready] = useAxiosLoader();

  return (
    <>
      {children}
    </>
  )
}

export default AbstractHoc;
