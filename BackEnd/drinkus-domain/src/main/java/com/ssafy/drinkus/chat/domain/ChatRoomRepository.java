package com.ssafy.drinkus.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<Chat, Long> {
}
