import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface CommonState {
  user: any,
}

const initialState: CommonState = {
  user: undefined,
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

export const { setUser, setShowLoginGuideModal } = commonSlice.actions;
export default commonSlice;
