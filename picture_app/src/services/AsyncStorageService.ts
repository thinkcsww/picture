import AsyncStorage from "@react-native-async-storage/async-storage";

const AsyncStorageService = {
  Keys: {
    AccessToken: 'AccessToken',
    RefreshToken: 'RefreshToken',
  },
  getStringData: async (key: string) => {
    try {
      const value = await AsyncStorage.getItem(key)
      if(value !== null) {
        return value;
      }

    } catch(e: any) {
      console.log(e.message)
    }
  },
  getObjectData: async (key: string) => {
    try {
      const jsonValue = await AsyncStorage.getItem('@storage_Key')
      return jsonValue != null ? JSON.parse(jsonValue) : null;

    } catch(e: any) {
      console.log(e.message)
    }
  },
  setStringData: async (key: string, value: string) => {
    try {
      await AsyncStorage.setItem(key, value)
    } catch (e: any) {
      console.log(e.message)
    }
  },
  setObjectData: async (key: string, value: any) => {
    try {
      const jsonValue = JSON.stringify(value)
      await AsyncStorage.setItem(key, jsonValue)
    } catch (e: any) {
      console.log(e.message)
    }
  }

}

export default AsyncStorageService;
