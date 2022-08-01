import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface SignUpState {
  signUpRedux: {
    username: string,
    token: string,
    destination: string,
  },
}

const initialState: SignUpState = {
  signUpRedux: {
    username: '',
    token: '',
    destination: '',
  },
};

export const signUpSlice = createSlice({
  name: "signUp",
  initialState,
  reducers: {
    setSignUpRedux(state, action: PayloadAction<any>) {
      state.signUpRedux = { ...state.signUpRedux, ...action.payload };
    },
  },
});

export const { setSignUpRedux } = signUpSlice.actions;
export default signUpSlice;
