import React, { FC, useState } from "react";
import { FlatList, SafeAreaView, StyleSheet, View } from "react-native";
import { NavigationProp } from "@react-navigation/native";
import TabListSpecialtySelectModal from "./components/TabListSpecialtySelectModal";
import { Seller } from "../../types/Seller";
import { SelectValue } from "../../types/SelectValue";
import { useInfiniteQuery } from "react-query";
import { SellerService } from "../../services/SellerService";
import { AxiosError } from "axios";
import TabListHeaderWithOptions from "../../components/tab-list/TabListHeaderWithOptions";
import TabListFilter from "../../components/tab-list/TabListFilter";
import { Specialty } from "../../types/Common";
import { PageResult, Result } from "../../types/Page";
import { Divider } from "react-native-paper";
import CommonNodata from "../../components/CommonNodata";
import SellerListItem from "./components/SellerListItem";

type SellerScreenProps = {
  navigation: NavigationProp<any>
}

const filterList = [
  new SelectValue('기본순', Seller.Filter.DEFAULT),
  new SelectValue('작업 많은순', Seller.Filter.MATCHING),
  new SelectValue('별점순', Seller.Filter.RATING),
  new SelectValue('리뷰 많은순', Seller.Filter.REVIEW),
  new SelectValue('가격 낮은순', Seller.Filter.PRICE),
  // new SelectValue('가격 높은순', Seller.Filter.PRICE_EXPENSIVE),
]

const SellerScreen: FC<SellerScreenProps> = ({ navigation }) => {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const [showSelector, setShowSelector] = useState(false);
  const [selectedSpecialty, setSelectedSpecialty] = useState(new SelectValue<Specialty>('인물 사진', Specialty.PEOPLE));
  const [selectedFilter, setSelectedFilter] = useState(new SelectValue<Seller.Filter>('기본순', Seller.Filter.DEFAULT));
  const [searchText, setSearchText] = useState('');
  const [isFetching, setIsFetching] = useState(false);

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const getSellersQuery = useInfiniteQuery([SellerService.QueryKey.getSellers, selectedSpecialty, selectedFilter, searchText], ({ pageParam = 0 }) => {
    return SellerService.getSellers(selectedSpecialty.value, selectedFilter.value, searchText, pageParam);
  }, {
    getNextPageParam: (lastPageData: Result<PageResult>) => {
      return lastPageData.data.last ? undefined : lastPageData.data.number + 1;
    },
    onSuccess: (result: any) => {
      console.log('==== Seller 리스트 조회 성공 ====');
      console.log(result);
    },
    onError: (err: AxiosError) => {
      console.log(err.message);
    },
    keepPreviousData: true,
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

  const onChangeSearchText = (text: string) => {
    setSearchText(text);
  }

  const onSelectFilter = (filter: SelectValue) => {
    setSelectedFilter(filter);
  }

  const onRefresh = async () => {
    setIsFetching(true);
    getSellersQuery.remove()
    getSellersQuery.refetch().then(() => {
      setIsFetching(false);
    });
  };

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  if (getSellersQuery.isLoading) {
    return null;
  }

  return <SafeAreaView style={styles.container}>
    { showSelector && <TabListSpecialtySelectModal selectedSpecialty={selectedSpecialty.value} close={onCloseSelector} onSelect={onSelectSelectorItem}/> }

    <TabListHeaderWithOptions onChangeSearchText={onChangeSearchText} searchText={searchText} selectedSpecialty={selectedSpecialty} onClickSelector={onClickSelector}/>
    <TabListFilter list={filterList} onPress={onSelectFilter} selectedFilter={selectedFilter}/>
    <FlatList
      data={getSellersQuery.data?.pages.map((page: Result<PageResult>) => page.data.content).flat()}
      onRefresh={onRefresh}
      refreshing={isFetching}
      ItemSeparatorComponent={() => <Divider style={{ height: 1, marginVertical: 20 }} />}
      keyExtractor={(item) => item.id}
      onEndReached={() => getSellersQuery.fetchNextPage()}
      onEndReachedThreshold={1}
      renderItem={({ item }) => {
        return <SellerListItem item={item} />;
      }}
      showsVerticalScrollIndicator={false}
      ListEmptyComponent={() => <CommonNodata />}
      ListFooterComponent={() => <View style={{ height: 30}}/>}
    />

  </SafeAreaView>
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  }
})

export default SellerScreen;
