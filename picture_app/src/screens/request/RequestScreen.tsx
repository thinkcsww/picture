import React, { FC, useState } from "react";
import { FlatList, SafeAreaView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { NavigationProp } from "@react-navigation/native";
import { SelectValue } from "../../types/SelectValue";
import { InfiniteData, useInfiniteQuery } from "react-query";
import { AxiosError } from "axios";
import TabListHeaderWithOptions from "../../components/tab-list/TabListHeaderWithOptions";
import TabListFilter from "../../components/tab-list/TabListFilter";
import TabListSpecialtySelectModal from "../seller/components/TabListSpecialtySelectModal";
import { RequestService } from "../../services/RequestService";
import { Specialty } from "../../types/Common";
import { Request } from "../../types/Request";
import { Colors } from "../../colors";
import { RouteNames } from "../../AppNav";
import { Divider } from "react-native-paper";
import RequestListItem from "./components/RequestListItem";
import CommonNodata from "../../components/CommonNodata";
import { PageResult } from "../../types/Page";

type RequestScreenProps = {
  navigation: NavigationProp<any>
}

const filterList = [
  new SelectValue('기본순', Request.Filter.DEFAULT),
  new SelectValue('의뢰비순', Request.Filter.PRICE),
  new SelectValue('마감임박순', Request.Filter.DUE_DATE),
]

const RequestScreen: FC<RequestScreenProps> = ({ navigation }) => {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const [showSelector, setShowSelector] = useState(false);
  const [selectedSpecialty, setSelectedSpecialty] = useState(new SelectValue<Specialty>('인물 사진', Specialty.PEOPLE));
  const [selectedFilter, setSelectedFilter] = useState(new SelectValue<Request.Filter>('기본순', Request.Filter.DEFAULT));
  const [isFetching, setIsFetching] = useState(false);

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const getRequestsQuery = useInfiniteQuery([RequestService.QueryKey.getRequests, selectedSpecialty, selectedFilter], ({ pageParam = 0 }) => {
    return RequestService.getRequests(selectedSpecialty.value, selectedFilter.value, pageParam);
  }, {
    getNextPageParam: (lastPageData: PageResult) => {
      return lastPageData.last ? undefined : lastPageData.number + 1;
    },
    onSuccess: (result: InfiniteData<PageResult>) => {
      console.log('==== Request 리스트 조회 성공 ====');
      console.log(result);
    },
    onError: (err: AxiosError) => {
      console.log(err.message);
    },
    keepPreviousData: true
  })

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Functions
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const onCloseSelector = () => {
    setShowSelector(false);
  }

  const onClickSelector = () => {
    setShowSelector(true);
  }

  const onSelectSelectorItem = (specialty: SelectValue<Specialty>) => {
    setSelectedSpecialty(specialty);
    setShowSelector(false);
  }

  const onSelectFilter = (filter: SelectValue) => {
    setSelectedFilter(filter);
  }

  const onPressAddRequest = () => {
    navigation.navigate(RouteNames.AddRequest);
  }

  const onRefresh = async () => {
    setIsFetching(true);
    getRequestsQuery.remove()
    getRequestsQuery.refetch().then(() => {
      setIsFetching(false);
    });
  };

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  if (getRequestsQuery.isLoading) {
    return null;
  }



  return <SafeAreaView style={styles.container}>
    { showSelector && <TabListSpecialtySelectModal selectedSpecialty={selectedSpecialty.value} close={onCloseSelector} onSelect={onSelectSelectorItem}/> }

    <TabListHeaderWithOptions selectedSpecialty={selectedSpecialty} onClickSelector={onClickSelector}/>
    <TabListFilter list={filterList} onPress={onSelectFilter} selectedFilter={selectedFilter}/>

    <FlatList
      data={getRequestsQuery.data?.pages.map((page: PageResult) => page.content).flat()}
      onRefresh={onRefresh}
      refreshing={isFetching}
      ItemSeparatorComponent={() => <Divider style={{ height: 1, marginVertical: 20 }} />}
      keyExtractor={(item) => item.id}
      onEndReached={() => getRequestsQuery.fetchNextPage()}
      onEndReachedThreshold={1}
      renderItem={({ item }) => {
        return <RequestListItem item={item} />;
      }}
      showsVerticalScrollIndicator={false}
      ListEmptyComponent={() => <CommonNodata />}
      ListFooterComponent={() => <View style={{ height: 30}}/>}
    />


    <TouchableOpacity onPress={onPressAddRequest} style={{
      position: 'absolute',
      bottom: 15,
      right: 15,
      width: 70,
      height: 70,
      borderRadius: 35,
      backgroundColor: Colors.PRIMARY,
      alignItems: 'center',
      justifyContent: 'center'
    }}>
      <Text style={{
        fontSize: 50,
        color: 'white',
        fontWeight: '300'
      }}>+</Text>
    </TouchableOpacity>

  </SafeAreaView>
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  }
})

export default RequestScreen;
