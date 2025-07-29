package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import com.busanit501.shoppingweb_project.repository.search.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("select b from Product b where b.productId = :productId")
    Product findByProductId(@Param("productId") Long productId);

    // SELECT * FROM PRODUCT WHERE PRODUCTID = porductId
    List<Product> findByProductTag(ProductCategory productTag);

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:keyword%")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

}