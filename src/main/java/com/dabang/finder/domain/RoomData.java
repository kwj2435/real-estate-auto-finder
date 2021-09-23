package com.dabang.finder.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "room_data")
public class RoomData {

    @Id
    @Column(name = "data_idx")
    private Long dataIdx;
    @Column(name = "location_idx")
    private int locationIdx;
    @Column(name = "data_key")
    private String dataKey;
}
