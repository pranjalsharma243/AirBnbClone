package com.myprojects.projects.airbnb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name="hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    private String city;

    @Column(columnDefinition = "TEXT[]")  // postgres supports this -> photos TEXT[]
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Embedded
    private HotelContactInfo hotelContactInfo;

}
