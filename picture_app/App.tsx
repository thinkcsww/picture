/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */
import "react-native-gesture-handler";

import React from "react";
import { LogBox } from "react-native";
import AppNavContainer from "./src/AppNavContainer";
import { Provider } from "react-redux";
import { QueryClient, QueryClientProvider } from "react-query";
import store from "./src/store/config";

const App = () => {

  const queryClient = new QueryClient();
  LogBox.ignoreLogs(['image', 'source.uri'])

  return (
    <Provider store={store}>
      <QueryClientProvider client={queryClient}>
        <AppNavContainer />
      </QueryClientProvider>
    </Provider>

  );
};

export default App;

