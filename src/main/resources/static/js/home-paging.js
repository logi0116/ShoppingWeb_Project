// ==================================================
// [lsr/feature/paging] home.html 전용 페이징 및 검색 스크립트
// "수정보다는 추가" 원칙에 따라, 기존 HTML/JS를 건드리지 않고 기능을 덧씌웁니다.
// ==================================================

// 1. 상태 관리를 위한 전역 변수
let currentPage = 1;
let currentKeyword = "";
let currentCategory = "";

// 2. 페이지가 완전히 로드되면 페이징 기능을 초기화하고 이벤트를 연결합니다.
document.addEventListener("DOMContentLoaded", () => {

  // [lsr/fix] home.html의 displayProducts 함수를 덮어써서 페이징 객체와 배열 모두 처리 가능하도록 개선
  if (typeof displayProducts === "function") {
    const originalDisplayProducts = displayProducts; // 기존 함수 백업
    displayProducts = function (productsToShow) {
      // 데이터가 배열인지, 페이징 객체인지 확인하여 실제 상품 목록을 추출합니다.
      const products = Array.isArray(productsToShow)
        ? productsToShow
        : (productsToShow && productsToShow.dtoList); // productsToShow가 null이 아닌지 확인

      if (!products) {
        console.warn("표시할 상품 데이터가 없습니다.", productsToShow);
        // 상품이 없을 때 productGrid를 비워줍니다.
        const productGrid = document.getElementById("productGrid");
        if(productGrid) productGrid.innerHTML = "<p>상품이 없습니다.</p>";
        return;
      }
      // 백업해둔 원래 함수에 정제된 데이터를 넣어 호출
      originalDisplayProducts(products);
    };
  }

  // 2-1. 기존 window.onload의 역할을 완전히 대체하는 새로운 함수를 정의합니다.
  window.onload = function() {
    console.log("home-paging.js에 의해 재정의된 onload가 실행됩니다.");
    currentKeyword = '';
    currentCategory = '';
    fetchAndDisplayProducts(1);
  };

  // 2-2. 검색 버튼에 새로운 클릭 이벤트를 덧씌웁니다.
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

  // 2-4. 카테고리 카드들에 새로운 클릭 이벤트를 덧씌웁니다.
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

  // 2-5. '전체 보기' 버튼에도 새로운 이벤트를 연결합니다.
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
      // 덮어쓴 displayProducts 함수를 호출합니다.
      displayProducts(pageData); 
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
