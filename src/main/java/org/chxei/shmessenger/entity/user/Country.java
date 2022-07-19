package org.chxei.shmessenger.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(name = "countries")
@Table(name = "countries")
public final class Country {
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
