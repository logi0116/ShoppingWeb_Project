package com.busanit501.shoppingweb_project.repository.search;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

/**
 * QueryDSL을 사용하기 위한 사용자 정의 리포지토리 인터페이스
 * 역할: Spring Data JPA가 기본으로 제공하는 기능을 넘어,
 * 복잡한 조건(검색, 동적 쿼리 등)을 포함하는 페이징 쿼리를 정의하기 위함.
 * 이름 규칙: 반드시 '...RepositoryCustom' 형식으로 만들어야 JPA가 인식할 수 있음.
 */
public interface ProductRepositoryCustom {

    /**
     * 상품 목록을 페이징하여 반환하는 메소드 (향후 검색 기능 추가 예정)
     * 
     * @param pageRequestDTO 페이징 요청 정보 (페이지 번호, 사이즈)
     * @return 페이징 처리된 상품 데이터(Page<Product>)
     */
    Page<Product> search(PageRequestDTO pageRequestDTO);

}