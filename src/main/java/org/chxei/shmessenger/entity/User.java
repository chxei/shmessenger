package org.chxei.shmessenger.entity;

import lombok.Data;
import org.chxei.shmessenger.utils.PostgreSQLEnumType;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity(name = "users")
@DynamicInsert
@Table(name="users")
@TypeDef(
        name = "PGSQL_ENUM",
        typeClass = PostgreSQLEnumType.class
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userName;
    private String name;
    @ColumnDefault("now()")
    private Timestamp creationDate;
    private String countryCode;
    private String email;
    private String phone;
    //@JsonDeserialize(using= OptimizedTimestampDeserializer.class)
    private Timestamp birthDate;
    private String pathToProfilePicture;
    private String pathToBackgroundPicture;
    private String password;
    private boolean isActive;
    private boolean isVerified;

    @Enumerated(EnumType.STRING)
    @Type( type = "PGSQL_ENUM" )
    @Column(name = "gender")
    private Gender gender;

    public User(String userName, String name, String countryCode, String email, String phone, Timestamp birthDate, String pathToProfilePicture, String pathToBackgroundPicture, String password, Gender gender) {
        java.util.Date utilDate = new java.util.Date();
        this.userName = userName;
        this.name = name;
        this.creationDate = new Timestamp(utilDate.getTime());
        this.countryCode = countryCode;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.pathToProfilePicture = pathToProfilePicture;
        this.pathToBackgroundPicture = pathToBackgroundPicture;
        this.password = password;
        this.isActive = true;
        this.isVerified = false;
        this.gender = gender;
    }

    public User (){}

    public enum Gender {
        male,
        female,
        other
    }

}

