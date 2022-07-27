import React, { FC, useState } from "react";
import { SafeAreaView, StyleSheet } from "react-native";
import { NavigationProp } from "@react-navigation/native";
import { SelectValue } from "../../types/SelectValue";
import { useQuery } from "react-query";
import { SellerService } from "../../services/SellerService";
import { AxiosError } from "axios";
import TabListHeader from "../../components/tab-list/TabListHeader";
import TabListFilter from "../../components/tab-list/TabListFilter";
import TabListSpecialtySelectModal from "../seller/components/TabListSpecialtySelectModal";
import RequestList from "./components/RequestList";
import { RequestService } from "../../services/RequestService";
import { Specialty } from "../../types/Common";
import { Request } from "../../types/Request";

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

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const getRequestsQuery = useQuery([RequestService.QueryKey.getRequests, selectedSpecialty, selectedFilter], () => {
    return RequestService.getRequests(selectedSpecialty.value, selectedFilter.value);
  }, {
    onSuccess: (result: any) => {
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

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  if (getRequestsQuery.isLoading) {
    return null;
  }


  return <SafeAreaView style={styles.container}>
    { showSelector && <TabListSpecialtySelectModal selectedSpecialty={selectedSpecialty.value} close={onCloseSelector} onSelect={onSelectSelectorItem}/> }

    <TabListHeader selectedSpecialty={selectedSpecialty} onClickSelector={onClickSelector}/>
    <TabListFilter list={filterList} onPress={onSelectFilter} selectedFilter={selectedFilter}/>
    <RequestList list={getRequestsQuery.data.content}/>

  </SafeAreaView>
}

const styles = StyleSheet.create({
  container: {
    flex: 1
  }
})

export default RequestScreen;
