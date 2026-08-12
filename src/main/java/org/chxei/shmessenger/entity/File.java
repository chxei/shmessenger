package org.chxei.shmessenger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @JdbcTypeCode(SqlTypes.VARBINARY)
    private byte[] fileObject;
    @Lob
    @JdbcTypeCode(SqlTypes.VARBINARY)
    private byte[] fileCompressed;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof File file)) {
            return false;
        }
        return id != null && id.equals(file.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
