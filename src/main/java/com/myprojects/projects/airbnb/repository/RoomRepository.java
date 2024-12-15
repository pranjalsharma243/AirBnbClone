package com.myprojects.projects.airbnb.repository;

import com.myprojects.projects.airbnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
