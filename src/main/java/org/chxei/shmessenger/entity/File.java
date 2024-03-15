package org.chxei.shmessenger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "files")
@Entity
@Getter
@Setter
@ToString
public final class File {
    boolean isStoredLocally;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Lob
    private byte[] fileObject;
    @Lob
    private byte[] fileCompressed;

}
