package com.dabang.finder.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "room_data")
public class RoomData {

    @Id
    @Column(name = "data_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dataIdx;
    @Column(name = "location_idx")
    private int locationIdx;
    @Column(name = "data_key")
    private String dataKey;
}
