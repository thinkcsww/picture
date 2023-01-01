package com.applory.pictureserver.domain.matching;

import java.util.List;

public interface MatchingRepositoryCustom {
    List<Matching> findBySearch(MatchingDto.Search search);
}
