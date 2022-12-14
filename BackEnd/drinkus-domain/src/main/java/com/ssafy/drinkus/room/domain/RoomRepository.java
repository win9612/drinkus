package com.ssafy.drinkus.room.domain;

import com.ssafy.drinkus.category.domain.Category;
import com.ssafy.drinkus.common.type.YN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {

    // 방 번호에 따라 방 제거
    void deleteById(Long roomId);

    // 기본 방리스트의 방정보 반환형
    @Query("select r.roomId, r.roomName, r.createdDate, r.peopleLimit, r.placeTheme from Room as r")
    Optional<List<Room>> findRoomList();

    // 같은 나잇대의 방
    Optional<List<Room>> findTop4ByAges20OrderByCreatedDateDesc(YN ages20);
    Optional<List<Room>> findTop4ByAges30OrderByCreatedDateDesc(YN ages30);
    Optional<List<Room>> findTop4ByAges40OrderByCreatedDateDesc(YN ages40);
    Optional<List<Room>> findTop4ByAges50OrderByCreatedDateDesc(YN ages50);
    Optional<List<Room>> findTop4ByAges60OrderByCreatedDateDesc(YN ages60);
    Optional<List<Room>> findTop4ByAges70OrderByCreatedDateDesc(YN ages70);

    // 1시간 이내 생성된 방
    Optional<List<Room>> findTop8ByCreatedDateAfterOrderByCreatedDateDesc(LocalDateTime createdDate);
    Optional<List<Room>> findAllByCreatedDateAfter(LocalDateTime createdDate);

    // 방 존재여부
    Boolean existsByRoomId(Long roomId);

    // 카테고리 ID로 방 리스트 조회
    List<Room> findTop4ByCategory(Category category);
}
