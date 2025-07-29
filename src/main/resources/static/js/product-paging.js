// IIFE (Immediately Invoked Function Expression)
(function () {
  document.addEventListener("DOMContentLoaded", function () {
    const productGrid = document.getElementById("productGrid");
    const paginationContainer = document.getElementById("pagination");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.querySelector(".search-container button");

    if (!productGrid) return; // home.html이 아니면 실행 중단

    // 1. 서버에 상품 데이터 요청하는 메인 함수
    function getProducts(page = 1, size = 8, type = "nt", keyword = "") {
      const params = new URLSearchParams({
        page: page,
        size: size,
        type: type,
        keyword: keyword,
      });

      fetch(`/api/products?${params.toString()}`)
        .then((response) => {
          if (!response.ok) throw new Error("Network response was not ok");
          return response.json();
        })
        .then((data) => {
          console.log("Server Response:", data);
          displayProducts(data.dtoList);
          setupPagination(data); // 백엔드에서 온 페이지 정보로 UI 생성
        })
        .catch((error) => {
          console.error("Error fetching products:", error);
          productGrid.innerHTML = "<p>상품을 불러오는 데 실패했습니다.</p>";
        });
    }

    // 2. 상품 목록을 화면에 표시하는 함수
    function displayProducts(products) {
      productGrid.innerHTML = "";
      if (!products || products.length === 0) {
        productGrid.innerHTML = "<p>상품이 없습니다.</p>";
        return;
      }

      products.forEach((product) => {
        const productCard = document.createElement("div");
        productCard.className = "product-card";
        productCard.innerHTML = `
                    <div class="product-image">${
                      product.image || "이미지 없음"
                    }</div>
                    <div class="product-info">
                        <h3 onclick="goToProductDetail(${product.productId})">${
          product.productName
        }</h3>
                        <p>카테고리: ${product.productTag}</p>
                        <div class="product-price">${Number(
                          product.price
                        ).toLocaleString()}원</div>
                        <button class="add-to-cart-btn" onclick="addToCart(${
                          product.productId
                        })">
                            장바구니 담기
                        </button>
                    </div>
                `;
        productGrid.appendChild(productCard);
      });
    }

    // 3. 백엔드 데이터를 기반으로 페이지네이션 UI 생성
    function setupPagination(pageData) {
      paginationContainer.innerHTML = "";
      if (!pageData || pageData.totalCount === 0) return;

      const createPageButton = (text, pageNum) => {
        const btn = document.createElement("button");
        btn.innerText = text;
        btn.className = "pagination-button";
        btn.addEventListener("click", () =>
          getProducts(pageNum, 8, searchInput.value)
        );
        return btn;
      };

      // '이전' 버튼
      if (pageData.prev) {
        const prevBtn = createPageButton("이전", pageData.start - 1);
        prevBtn.classList.add("prev-next");
        paginationContainer.appendChild(prevBtn);
      }

      // 페이지 번호 버튼
      pageData.pageNumList.forEach((pageNum) => {
        const btn = createPageButton(pageNum, pageNum);
        if (pageNum === pageData.page) {
          btn.classList.add("active");
        }
        paginationContainer.appendChild(btn);
      });

      // '다음' 버튼
      if (pageData.next) {
        const nextBtn = createPageButton("다음", pageData.end + 1);
        nextBtn.classList.add("prev-next");
        paginationContainer.appendChild(nextBtn);
      }
    }

    // 4. 검색 및 카테고리 클릭 기능 초기화
    function initializeEventListeners() {
      // 검색 버튼 클릭
      if (searchButton) {
        searchButton.onclick = (event) => {
          event.preventDefault();
          getProducts(1, 8, "nt", searchInput.value); // 'nt' 타입으로 전체 검색
        };
      }
      // 검색창 엔터
      searchInput.addEventListener("keyup", (event) => {
        if (event.key === "Enter") {
          getProducts(1, 8, "nt", searchInput.value);
        }
      });

      // 카테고리 카드 클릭
      const categoryCards = document.querySelectorAll(".category-card");
      categoryCards.forEach((card) => {
        card.addEventListener("click", () => {
          const category = card.dataset.category;
          searchInput.value = ""; // 카테고리 클릭 시 검색창은 비움
          getProducts(1, 8, "t", category); // 't' 타입으로 카테고리 검색
        });
      });
    }

    // 5. 실행!
    initializeEventListeners();
    getProducts(); // 페이지 첫 로드 시 1페이지 데이터 요청
  });
})();
