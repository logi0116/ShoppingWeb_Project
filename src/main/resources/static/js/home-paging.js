// ==================================================
// [lsr/feature/paging] home.html 전용 페이징 및 검색 스크립트 (디버깅 버전)
// ==================================================

console.log("DEBUG: home-paging.js 파일 로드됨");

// 1. 상태 관리를 위한 전역 변수
let currentPage = 1;
let currentKeyword = "";
let currentCategory = "";

// 2. 페이지의 모든 리소스가 로드된 후 페이징 기능 실행
window.addEventListener("load", () => {
  console.log("DEBUG: window.load 이벤트 발생. 페이징 초기화 시작.");
  initializePaging();
});

// 3. 페이징 기능 초기화 및 이벤트 리스너 연결
function initializePaging() {
  console.log("DEBUG: initializePaging() 함수 호출됨.");
  // 초기 상품 목록 로딩
  fetchAndDisplayProducts(1);

  // 검색 버튼 이벤트 연결
  const searchButton = document.querySelector(".search-container button");
  if (searchButton) {
    console.log("DEBUG: 검색 버튼 이벤트 리스너 연결됨.");
    searchButton.onclick = (event) => {
      event.preventDefault(); 
      const searchInput = document.getElementById("searchInput");
      currentKeyword = searchInput.value.trim();
      currentCategory = "";
      console.log(`DEBUG: 검색 실행. Keyword: ${currentKeyword}`);
      fetchAndDisplayProducts(1);
    };
  }

  // 엔터 키 이벤트 연결
  const searchInput = document.getElementById("searchInput");
  if (searchInput) {
    searchInput.addEventListener("keypress", function (event) {
      if (event.key === "Enter") {
        event.preventDefault();
        if(searchButton) searchButton.click();
      }
    });
  }

  // 카테고리 카드 이벤트 연결
  const categoryCards = document.querySelectorAll(".category-card");
  categoryCards.forEach((card) => {
    const categoryH3 = card.querySelector("h3");
    if (!categoryH3) return;

    const category = categoryH3.textContent;
    let categoryValue = category;
    if (category === "바지") categoryValue = "하의";
    else if (category === "악세서리") categoryValue = "액세서리";
    else if (category.includes("기타")) categoryValue = "기타";

    card.onclick = (event) => {
      event.preventDefault();
      currentKeyword = "";
      currentCategory = categoryValue;
      console.log(`DEBUG: 카테고리 필터링 실행. Category: ${currentCategory}`);
      fetchAndDisplayProducts(1);
    };
  });

  // '전체 보기' 버튼 이벤트 연결
  const showAllBtn = document.querySelector(".show-all-btn");
  if (showAllBtn) {
    console.log("DEBUG: '전체 보기' 버튼 이벤트 리스너 연결됨.");
    showAllBtn.onclick = (event) => {
      event.preventDefault();
      currentKeyword = "";
      currentCategory = "";
      console.log("DEBUG: '전체 보기' 실행.");
      fetchAndDisplayProducts(1);
    };
  }

  // 로그아웃 버튼 이벤트 연결
  const logoutLink = document.querySelector('a[href="/logout"]');
  if (logoutLink) {
    console.log("DEBUG: 로그아웃 링크 이벤트 리스너 연결됨.");
    logoutLink.onclick = (event) => {
      event.preventDefault();
      // Spring Security의 로그아웃 URL로 이동
      window.location.href = "/logout";
    };
  }
}

/**
 * 4. 상품 목록과 페이지네이션을 가져와서 화면에 표시하는 통합 함수
 * @param {number} page - 요청할 페이지 번호
 */
function fetchAndDisplayProducts(page) {
  currentPage = page;
  console.log(`DEBUG: fetchAndDisplayProducts(${page}) 호출됨. API 요청 시작.`);

  let apiUrl = `/api/products?page=${page}`;
  if (currentKeyword) {
    apiUrl += `&type=n&keyword=${encodeURIComponent(currentKeyword)}`;
  } else if (currentCategory) {
    apiUrl += `&type=t&keyword=${encodeURIComponent(currentCategory)}`;
  }

  fetch(apiUrl)
    .then((response) => {
      console.log("DEBUG: API 응답 받음.", response);
      if (!response.ok) throw new Error("데이터 로딩 실패");
      return response.json();
    })
    .then((pageData) => {
      console.log("DEBUG: JSON 파싱 성공. 받은 데이터:", pageData);
      const productGrid = document.getElementById("productGrid");
      if (!productGrid) {
        console.error("DEBUG ERROR: productGrid 요소를 찾을 수 없음!");
        return;
      }
      
      productGrid.innerHTML = ""; // 화면을 깨끗하게 비웁니다.

      if (pageData.dtoList && pageData.dtoList.length > 0) {
        console.log(`DEBUG: ${pageData.dtoList.length}개의 상품을 화면에 그립니다.`);
        pageData.dtoList.forEach((product) => {
          const productCard = document.createElement("div");
          productCard.className = "product-card";
          productCard.innerHTML = `
            <div class="product-image">${product.image || "이미지 없음"}</div>
            <div class="product-info">
                <h3 onclick="paging_goToProductDetail(${product.productId})">${product.productName}</h3>
                <p>카테고리: ${product.productTag}</p>
                <div class="product-price">${Number(product.price).toLocaleString()}원</div>
                <button class="add-to-cart-btn" onclick="paging_addToCart(${product.productId}, '${product.productName}')">
                    장바구니 담기
                </button>
            </div>
          `;
          productGrid.appendChild(productCard);
        });
      } else {
        console.log("DEBUG: 표시할 상품이 없습니다.");
        productGrid.innerHTML = "<p>상품이 없습니다.</p>";
      }
      
      console.log("DEBUG: 페이지네이션 렌더링 시작.");
      renderPagination(pageData);
    })
    .catch((error) => {
      console.error("DEBUG ERROR: fetch 과정에서 오류 발생!", error);
      const productGrid = document.getElementById("productGrid");
      if (productGrid) {
        productGrid.innerHTML = `<p>상품을 불러오는 중 오류가 발생했습니다.</p>`;
      }
    });
}

/**
 * 5. 페이지네이션 버튼을 렌더링하는 함수
 * @param {object} pageData - 서버에서 받은 PageResponseDTO 객체
 */
function renderPagination(pageData) {
  const paginationContainer = document.getElementById("pagination");
  if (!paginationContainer) {
    console.error("DEBUG ERROR: paginationContainer 요소를 찾을 수 없음!");
    return;
  }

  paginationContainer.innerHTML = "";

  if (pageData.prev) {
    const prevBtn = document.createElement("a");
    prevBtn.href = "#";
    prevBtn.className = "page-link";
    prevBtn.innerText = "이전";
    prevBtn.onclick = (e) => {
      e.preventDefault();
      fetchAndDisplayProducts(pageData.prevPage); // 수정: prevPage 사용
    };
    paginationContainer.appendChild(prevBtn);
  }

  pageData.pageNumList.forEach((pageNum) => {
    const pageBtn = document.createElement("a");
    pageBtn.href = "#";
    pageBtn.innerText = pageNum;
    pageBtn.className = "page-link";
    if (pageNum === pageData.current) {
      pageBtn.classList.add("active");
    }
    pageBtn.onclick = (e) => {
      e.preventDefault();
      fetchAndDisplayProducts(pageNum);
    };
    paginationContainer.appendChild(pageBtn);
  });

  if (pageData.next) {
    const nextBtn = document.createElement("a");
    nextBtn.href = "#";
    nextBtn.className = "page-link";
    nextBtn.innerText = "다음";
    nextBtn.onclick = (e) => {
      e.preventDefault();
      fetchAndDisplayProducts(pageData.nextPage); // 수정: nextPage 사용
    };
    paginationContainer.appendChild(nextBtn);
  }
  console.log("DEBUG: 페이지네이션 렌더링 완료.");
}

// 6. home.html의 함수들과의 충돌을 피하기 위해, 고유한 이름으로 함수를 새로 정의합니다.
function paging_goToProductDetail(productId) {
  window.location.href = `/products/${productId}`;
}

function paging_addToCart(productId, productName) {
  const payload = {
    productId: productId,
    quantity: 1,
  };

  fetch("/api/cart", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  })
    .then((res) => {
      if (res.status === 401) { // 401 Unauthorized (로그인 필요)
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
      }
      if (!res.ok) {
        throw new Error("장바구니 추가에 실패했습니다.");
      }
      return res.json();
    })
    .then((data) => {
      if(data) {
        alert(`🛒 ${productName}이(가) 장바구니에 추가되었습니다.`);
        // home.html의 updateCartCount 함수를 호출합니다.
        if(typeof updateCartCount === 'function') {
            updateCartCount();
        }
      }
    })
    .catch((err) => {
      console.error(err);
      alert("장바구니 추가 중 오류가 발생했습니다.");
    });
}
