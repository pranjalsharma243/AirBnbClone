package com.myprojects.projects.airbnb.repository;

import com.myprojects.projects.airbnb.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

}
