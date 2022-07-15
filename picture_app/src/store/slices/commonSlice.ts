import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface CommonState {
  user: any,
}

const initialState: CommonState = {
  user: {},
};

export const commonSlice = createSlice({
  name: "common",
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<any>) {
      state.user = action.payload;
    },
  },
});

export const { setUser } = commonSlice.actions;
export default commonSlice;
