package com.dabang.finder.service;

import com.dabang.finder.domain.RoomData;
import com.dabang.finder.repository.RoomRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScanServiceTests {
    @Autowired
    private RoomRepository roomRepository;

    @Test
    public void findAllByLocationId() {
        List<RoomData> list = roomRepository.findAllByLocationIdx(1);

        RoomData roomData = new RoomData();
        roomData.setDataKey("test2");
        roomData.setLocationIdx(1);
        list.add(roomData);
        roomRepository.save(roomData);

        Assert.assertEquals(list.get(0).getLocationIdx(), 1);
    }
}
