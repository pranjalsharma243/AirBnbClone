package com.myprojects.projects.airbnb.dto;

import com.myprojects.projects.airbnb.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomPriceDto {

    private Room room;
    private Double price;
}
