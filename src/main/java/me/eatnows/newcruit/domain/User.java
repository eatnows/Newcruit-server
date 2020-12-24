package me.eatnows.newcruit.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class User extends UpdateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    private Byte phone;
    @Column(name = "PROFILE_URL")
    private String profileUrl;
    private String introduce;
    private String authority;
    private Boolean dropuser;
}
