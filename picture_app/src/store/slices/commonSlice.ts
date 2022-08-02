import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface CommonState {
  user: any,
  isTokenExist: boolean
}

const initialState: CommonState = {
  user: undefined,
  isTokenExist: false,
};

export const commonSlice = createSlice({
  name: "common",
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<any>) {
      state.user = action.payload;
    },
    setIsTokenExist(state, action: PayloadAction<boolean>) {
      state.isTokenExist = action.payload;
    }
  },
});

export const { setUser, setIsTokenExist } = commonSlice.actions;
export default commonSlice;
