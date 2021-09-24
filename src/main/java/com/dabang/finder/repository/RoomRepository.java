package com.dabang.finder.repository;

import com.dabang.finder.domain.RoomData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomData, Long> {

    List<RoomData> findAllByLocationIdx(int locationIdx);
}
