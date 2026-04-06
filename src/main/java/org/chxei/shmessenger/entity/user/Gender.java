package org.chxei.shmessenger.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@Setter
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gender)) return false;
        Gender gender = (Gender) o;
        return name != null && name.equals(gender.getName());
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : getClass().hashCode();
    }
}
