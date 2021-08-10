package org.chxei.shmessenger.entity.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@Entity(name = "genders")
@Table(name = "genders")
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String name;

    private boolean active = true;

    public Gender(String name) {
        this.name = name;
    }

    public Gender() {
    }
}
