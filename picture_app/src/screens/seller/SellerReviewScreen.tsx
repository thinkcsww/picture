import React, { useState } from "react";
import { FlatList, SafeAreaView, View } from "react-native";
import { useInfiniteQuery } from "react-query";
import { ReviewService } from "../../services/ReviewService";
import { Divider } from "react-native-paper";
import CommonNodata from "../../components/CommonNodata";
import { AxiosError } from "axios";
import SellerReviewListItem from "./SellerReviewListItem";

const SellerReviewScreen = ({ route, navigation }: any) => {
  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Hooks
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const { id } = route.params;

  const getReviewsQuery = useInfiniteQuery(ReviewService.QueryKey.getReviews, ({ pageParam = 0}) => {
    return ReviewService.getReviews(id, pageParam);
  }, {
    getNextPageParam: (lastPageData: any) => {
      return lastPageData.data.last ? undefined : lastPageData.data.number + 1;
    },
    onSuccess: (result: any) => {
      console.log('==== Review 리스트 조회 성공 ====');
      console.log(result);
    },
    onError: (err: AxiosError) => {
      console.log(err.message);
    },
    keepPreviousData: true
  });

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | State Variables
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const [isFetching, setIsFetching] = useState(false);

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Functions
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  const onRefresh = async () => {
    setIsFetching(true);
    getReviewsQuery.remove()
    getReviewsQuery.refetch().then(() => {
      setIsFetching(false);
    });
  };

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  | Mark Up
  |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  return (
    <SafeAreaView style={{ flex: 1 }}>
      <FlatList
        data={getReviewsQuery.data?.pages.map((page: any) => page.data.content).flat()}
        onRefresh={onRefresh}
        refreshing={isFetching}
        ItemSeparatorComponent={() => <Divider style={{ height: 1, marginVertical: 20 }} />}
        keyExtractor={(item) => item.id}
        onEndReached={() => getReviewsQuery.fetchNextPage()}
        onEndReachedThreshold={1}
        renderItem={({ item }) => {
          return <SellerReviewListItem item={item} />;
        }}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={() => <CommonNodata />}
        ListFooterComponent={() => <View style={{ height: 30}}/>}
      />
    </SafeAreaView>
  )
}

export default SellerReviewScreen;
