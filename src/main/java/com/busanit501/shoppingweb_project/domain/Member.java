package com.busanit501.shoppingweb_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    // 푸쉬 테스트
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String memberId;

    private String email;
    private String password;

    @Column(name = "user_name")
    private String userName;

    private String phone;
    private LocalDate birthDate;
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String role;


    @Column(nullable = false)
    private boolean social;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    public void settingRole(String role){
        this.role = role;
    }

}
