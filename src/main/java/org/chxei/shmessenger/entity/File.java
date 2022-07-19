package org.chxei.shmessenger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

@Table(name = "files")
@Entity
@Getter
@Setter
@ToString
public final class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private byte[] file;

    boolean isStoredLocally;

    @Lob
    private byte[] fileCompressed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        File file = (File) o;

        return Objects.equals(id, file.id);
    }
}