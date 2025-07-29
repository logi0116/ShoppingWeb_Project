package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/home")
    public String home(Model model) {
//        List<ProductDTO> products = productService.getAllProducts();
//        for(ProductDTO product : products){
//            log.info("Controller 에서 product 이름 확인하는중 : " +  product.getProductName());
//        }
//        model.addAttribute("products", products);
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id , Model model) {
        model.addAttribute("productId", id);
        return "product-detail";
        // 화면만 그렸음 대신( productId라는 데이터 하나만 가지고)
    }
}