package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.Order;
import com.busanit501.shoppingweb_project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select b from Order b where b.orderId = :orderId")
    Order findByOrderId(@Param("orderId") Long orderId);

    List<Order> findByMember(Member member);

}
