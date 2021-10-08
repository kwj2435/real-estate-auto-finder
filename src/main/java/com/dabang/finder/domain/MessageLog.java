package com.dabang.finder.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Table(name = "message_log")
@NoArgsConstructor
public class MessageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seq;
    private String content;
    @Column(name = "createDate")
    private Date createDate;

    public MessageLog(String content) {
        this.content = content;
    }

    @PrePersist
    private void onCreate() {
        this.createDate = new Date();
    }
}
