package com.example.register.courses.catalog.room;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false, unique = true, length = 50)
    private String roomCode;

    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @Column(length = 100)
    private String building;

    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Integer isPublic = 1;
}