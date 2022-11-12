import { Platform } from "react-native";

export const Env = {
  host: Platform.OS === 'ios' ? 'http://localhost:8080' : 'http://192.168.200.110:8080',
}
