package com.applory.pictureserver.domain.chatting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChattingMessageRepository extends JpaRepository<ChattingMessage, UUID> {
}
