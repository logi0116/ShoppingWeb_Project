package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("select b from OrderItem b where b.orderItemId = :orderItemId")
    OrderItem findByOrderItemId(@Param("orderItemId") Long orderItemId);

    // 특정 회원이 특정 상품을 구매한 이력이 있는지 확인하는 쿼리
    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN TRUE ELSE FALSE END " +
            "FROM OrderItem oi " +
            "WHERE oi.order.member.memberId = :memberId AND oi.product.productId = :productId")
    boolean existsByMemberAndProduct(@Param("memberId") String memberId, @Param("productId") Long productId);
}
