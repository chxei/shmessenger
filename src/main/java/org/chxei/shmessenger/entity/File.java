package org.chxei.shmessenger.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "files")
@Entity
@Getter
@Setter
@ToString
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    @Lob
    private byte[] file;

    @Column
    boolean isStoredLocally;
    @Column
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