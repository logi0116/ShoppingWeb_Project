package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.CartItem;
import com.busanit501.shoppingweb_project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("select b from CartItem b where b.memberId = :memberId")
    List<CartItem> findByMemberId(@Param("memberId") Long memberId);

    void deleteByMemberId(@Param("memberId") Long memberId);

    Optional<CartItem> findByMemberIdAndProduct(Long memberId, Product product);

    void deleteByMemberIdAndProduct(Long memberId, Product product);

    long countByMemberId(Long memberId);
}
