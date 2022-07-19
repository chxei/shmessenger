package org.chxei.shmessenger.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Entity(name = "genders")
@Table(name = "genders")
public final class Gender {
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
