package com.applory.pictureserver.domain.user.querydto;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class SellerListVM {
    private String id;
    private String nickname;
    private String description;
    private Double rateAvg;
    private Long reviewCnt;
    private Integer price;
    private Long completeMatchingCnt;
    private String fileName;
}
