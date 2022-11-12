import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface SignUpState {
  signUpRedux: {
    username: string,
    token: string,
    destination: {
      key: string,
      params: any
    },
  },
}

const initialState: SignUpState = {
  signUpRedux: {
    username: '',
    token: '',
    destination: {
      key: '',
      params: {}
    },
  },
};

export const signUpSlice = createSlice({
  name: "signUp",
  initialState,
  reducers: {
    setSignUpRedux(state, action: PayloadAction<{ username?: string, token?: string, destination?: { key: string, params: any }}>) {
      state.signUpRedux = { ...state.signUpRedux, ...action.payload };
    },
  },
});

export const { setSignUpRedux } = signUpSlice.actions;
export default signUpSlice;
