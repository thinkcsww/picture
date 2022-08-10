import React, { FC, useEffect, useState } from "react";
import { SafeAreaView, StyleSheet } from "react-native";
import { NavigationProp } from "@react-navigation/native";
import TabListSpecialtySelectModal from "./components/TabListSpecialtySelectModal";
import { Seller } from "../../types/Seller";
import { SelectValue } from "../../types/SelectValue";
import SellerList from "./components/SellerList";
import { useQuery } from "react-query";
import { SellerService } from "../../services/SellerService";
import { AxiosError } from "axios";
import TabListHeader from "../../components/tab-list/TabListHeader";
import TabListFilter from "../../components/tab-list/TabListFilter";
import { Specialty } from "../../types/Common";
import AsyncStorageService from "../../services/AsyncStorageService";

type SellerScreenProps = {
  navigation: NavigationProp<any>
}

const filterList = [
  new SelectValue('기본순', Seller.Filter.DEFAULT),
  new SelectValue('채택률 높은순', Seller.Filter.CLOSED),
  new SelectValue('별점순', Seller.Filter.RATING),
  new SelectValue('리뷰 많은순', Seller.Filter.REVIEW),
  new SelectValue('가격 낮은순', Seller.Filter.PRICE_CHEAP),
  new SelectValue('가격 높은순', Seller.Filter.PRICE_EXPENSIVE),
]

const SellerScreen: FC<SellerScreenProps> = ({ navigation }) => {

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const [showSelector, setShowSelector] = useState(false);
  const [selectedSpecialty, setSelectedSpecialty] = useState(new SelectValue<Specialty>('인물 사진', Specialty.PEOPLE));
  const [selectedFilter, setSelectedFilter] = useState(new SelectValue<Seller.Filter>('기본순', Seller.Filter.DEFAULT));
  const [searchText, setSearchText] = useState('');

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const getSellersQuery = useQuery([SellerService.QueryKey.getSellers, selectedSpecialty, selectedFilter], () => {
    return SellerService.getSellers(selectedSpecialty.value, selectedFilter.value);
  }, {
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

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  if (getSellersQuery.isLoading || getSellersQuery.isError) {
    return null;
  }

  return <SafeAreaView style={styles.container}>
    { showSelector && <TabListSpecialtySelectModal selectedSpecialty={selectedSpecialty.value} close={onCloseSelector} onSelect={onSelectSelectorItem}/> }

    <TabListHeader onChangeSearchText={onChangeSearchText} searchText={searchText} selectedSpecialty={selectedSpecialty} onClickSelector={onClickSelector}/>
    <TabListFilter list={filterList} onPress={onSelectFilter} selectedFilter={selectedFilter}/>
    <SellerList list={getSellersQuery.data.content}/>

  </SafeAreaView>
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  }
})

export default SellerScreen;
