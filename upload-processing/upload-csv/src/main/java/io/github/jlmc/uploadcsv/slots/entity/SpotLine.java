package io.github.jlmc.uploadcsv.slots.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder

public class SpotLine {
    @CsvBindByName(column = "ID")
    private String id;
    @CsvBindByName(column = "Name")
    private String name;
    @CsvBindByName(column = "PhoneNumber")
    private String phoneNumber;
    @CsvBindByName(column = "Address")
    private String address;

}
