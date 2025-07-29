package com.busanit501.shoppingweb_project.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
public class OrderDTO {

    private Long orderid;
    // 나중에 어떤 order을 가져올지 알아야 해서

    private Long memberid;

    @CreatedDate
    @Column(name = "regDate", updatable = true)
    private LocalDateTime orderdate;

    private boolean status;
}
