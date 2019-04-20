package com.example.ugasoft.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "record")
public class Record {

    @Id
    @Column
    private long id;

    @Column
    private String value;

}
