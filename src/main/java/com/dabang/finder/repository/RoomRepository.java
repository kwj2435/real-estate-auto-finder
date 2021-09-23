package com.dabang.finder.repository;

import com.dabang.finder.domain.RoomData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomData, Long> {

    public List<RoomData> findAllByLocationIdx(int locationIdx);
}
