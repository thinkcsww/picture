package com.applory.pictureserver.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;



public interface FileRepository extends JpaRepository<File, String> {
}
