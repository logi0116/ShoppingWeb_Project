// ==================================================
// [lsr/feature/paging] home.html 전용 페이징 및 검색 스크립트
// ==================================================

// 1. 상태 관리를 위한 전역 변수
let currentPage = 1;
let currentKeyword = "";
let currentCategory = "";

// 2. 페이지가 로드되면 기존 onload를 덮어쓰고, 새로운 이벤트 리스너를 연결합니다.
document.addEventListener("DOMContentLoaded", () => {

  // 2-1. home.html의 window.onload 함수를 페이징을 지원하는 새 함수로 재정의(덮어쓰기)합니다.
  // 이것이 실행되면, 기존 onload는 무시되고 이 함수가 대신 실행됩니다.
  window.onload = function() {
    console.log("home-paging.js에 의해 재정의된 onload가 실행됩니다.");
    currentKeyword = '';
    currentCategory = '';
    fetchAndDisplayProducts(1);
  };

  // 2-2. 검색 버튼에 새로운 클릭 이벤트를 연결합니다.
  const searchButton = document.querySelector(".search-container button");
  if (searchButton) {
    searchButton.onclick = (event) => {
      event.preventDefault(); 
      const searchInput = document.getElementById("searchInput");
      currentKeyword = searchInput.value.trim();
      currentCategory = "";
      fetchAndDisplayProducts(1);
    };
  }

  // 2-3. 엔터 키 이벤트도 새로운 검색 기능으로 연결합니다.
  const searchInput = document.getElementById("searchInput");
  if (searchInput) {
    searchInput.addEventListener("keypress", function (event) {
      if (event.key === "Enter") {
        event.preventDefault();
        if(searchButton) searchButton.click();
      }
    });
  }

      currentCategory = "";
      fetchAndDisplayProducts(1);
    };
  }

  // 엔터 키 이벤트도 새로운 검색 기능으로 연결합니다.
  const searchInput = document.getElementById("searchInput");
  if (searchInput) {
    searchInput.addEventListener("keypress", function (event) {
      if (event.key === "Enter") {
        event.preventDefault();
        if(searchButton) searchButton.click();
      }
    });
  }

  // 카테고리 카드들에 새로운 클릭 이벤트를 덧씌웁니다.
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
      fetchAndDisplayProducts(1);
    };
  });

  // '전체 보기' 버튼에도 새로운 이벤트를 연결합니다.
  const showAllBtn = document.querySelector(".show-all-btn");
  if (showAllBtn) {
    showAllBtn.onclick = (event) => {
      event.preventDefault();
      currentKeyword = "";
      currentCategory = "";
      fetchAndDisplayProducts(1);
    };
  }
});

/**
 * 3. 상품 목록과 페이지네이션을 가져와서 화면에 표시하는 통합 함수
 * @param {number} page - 요청할 페이지 번호
 */
function fetchAndDisplayProducts(page) {
  currentPage = page;

  let apiUrl = `/api/products?page=${page}`;
  if (currentKeyword) {
    apiUrl += `&type=n&keyword=${encodeURIComponent(currentKeyword)}`;
  } else if (currentCategory) {
    apiUrl += `&type=t&keyword=${encodeURIComponent(currentCategory)}`;
  }

  fetch(apiUrl)
    .then((response) => {
      if (!response.ok) throw new Error("데이터 로딩 실패");
      return response.json();
    })
    .then((pageData) => {
      // home.html의 displayProducts를 직접 호출하지 않고, 여기서 화면을 직접 제어합니다.
      const productGrid = document.getElementById("productGrid");
      if (!productGrid) return;
      
      productGrid.innerHTML = ""; // 화면을 깨끗하게 비웁니다.
      if (pageData.dtoList && pageData.dtoList.length > 0) {
        pageData.dtoList.forEach((product) => {
          const productCard = document.createElement("div");
          productCard.className = "product-card";
          productCard.innerHTML = `
            <div class="product-image">${product.image || "이미지 없음"}</div>
            <div class="product-info">
                <h3 onclick="goToProductDetail(${product.productId})">${product.productName}</h3>
                <p>카테고리: ${product.productTag}</p>
                <div class="product-price">${Number(product.price).toLocaleString()}원</div>
                <button class="add-to-cart-btn" onclick="addToCart(${product.productId}, '${product.productName}')">
                    장바구니 담기
                </button>
            </div>
          `;
          productGrid.appendChild(productCard);
        });
      } else {
        productGrid.innerHTML = "<p>상품이 없습니다.</p>";
      }
      
      renderPagination(pageData);
    })
    .catch((error) => {
      console.error("Error:", error);
      const productGrid = document.getElementById("productGrid");
      if (productGrid) {
        productGrid.innerHTML = `<p>상품을 불러오는 중 오류가 발생했습니다.</p>`;
      }
    });
}

/**
 * 4. 페이지네이션 버튼을 렌더링하는 함수
 * @param {object} pageData - 서버에서 받은 PageResponseDTO 객체
 */
function renderPagination(pageData) {
  const paginationContainer = document.getElementById("pagination");
  if (!paginationContainer) return;

  paginationContainer.innerHTML = "";

  if (pageData.prev) {
    const prevBtn = document.createElement("a");
    prevBtn.href = "#";
    prevBtn.className = "page-link";
    prevBtn.innerText = "이전";
    prevBtn.onclick = (e) => {
      e.preventDefault();
      fetchAndDisplayProducts(pageData.start - 1);
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
      fetchAndDisplayProducts(pageData.end + 1);
    };
    paginationContainer.appendChild(nextBtn);
  }
}
