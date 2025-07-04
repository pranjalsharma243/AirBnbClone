package com.myprojects.projects.airbnb.repository;

import com.myprojects.projects.airbnb.entity.Hotel;
import com.myprojects.projects.airbnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {

    List<Hotel> findByOwner(User user);
}
