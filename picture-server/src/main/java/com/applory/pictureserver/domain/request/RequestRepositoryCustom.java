package com.applory.pictureserver.domain.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestRepositoryCustom {
    Page<Request> findRequestBySearchQ(RequestDto.Search search, Pageable pageable);
}
