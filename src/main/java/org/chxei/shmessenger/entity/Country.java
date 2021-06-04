package org.chxei.shmessenger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@Entity(name = "countries")
@Table(name = "countries")
public class Country {
    @Id
    private String code;
    private String name;

    private boolean isActive = true;

    public Country() {
    }

    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
