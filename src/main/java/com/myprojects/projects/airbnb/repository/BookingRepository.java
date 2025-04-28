package com.myprojects.projects.airbnb.repository;

import com.myprojects.projects.airbnb.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {


}
