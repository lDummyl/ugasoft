package com.example.ugasoft.data.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;


@NoArgsConstructor
@Data
public class Profile {

    @Id
    private String id;
    private String firstName;
    private String lastName;

    public Profile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
