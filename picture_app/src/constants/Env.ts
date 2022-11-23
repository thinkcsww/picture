import { Platform } from "react-native";

export const Env = {
  host: Platform.OS === 'ios' ? 'http://127.0.0.1:8080' : 'http://127.0.0.1:8080',
}
