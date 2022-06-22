package com.applory.pictureserver.domain.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID>, RequestRepositoryCustom {
    List<Request> findByUser_Id(UUID id);
}
