// 즉시 실행 함수 표현식(IIFE)을 사용하여 전역 스코프 오염을 방지합니다.
// 이 파일 전체가 하나의 독립된 프로그램처럼 동작하게 됩니다.
(function () {
  // DOM(Document Object Model)이 완전히 로드된 후에 스크립트를 실행합니다.
  // 이유: HTML 요소들이 모두 만들어지기 전에 Javascript가 실행되면,
  //      getElementById 같은 메소드가 요소를 찾지 못해 오류가 발생할 수 있습니다.
  document.addEventListener("DOMContentLoaded", function () {
    // 사용할 HTML 요소들을 미리 찾아 변수에 저장해 둡니다.
    const reviewList = document.getElementById("reviewList");
    const paginationContainer = document.getElementById("reviewPagination");

    // 이 스크립트는 product-detail.html에서만 동작해야 하므로,
    // reviewList 요소가 없으면 이후 코드를 실행하지 않고 종료합니다.
    if (!reviewList) return;

    // 현재 페이지의 URL 주소에서 상품 ID(productId)를 추출합니다.
    // 예: /products/12 -> 12를 추출
    const pathSegments = window.location.pathname.split('/');
    const productId = pathSegments[pathSegments.length - 1];

    // 1. 서버에 댓글 데이터를 요청하는 핵심 함수
    function getReviews(page = 1, size = 5) {
      // URL 쿼리 파라미터를 쉽게 만들기 위한 객체입니다.
      const params = new URLSearchParams({
        page: page,
        size: size,
      });

      // fetch API를 사용하여 백엔드에 GET 요청을 보냅니다. (AJAX 통신)
      // 상호작용: ReviewController의 @GetMapping("/{productId}") API가 호출됩니다.
      fetch(`/api/reviews/${productId}?${params.toString()}`)
        .then((response) => {
          // 서버 응답이 성공적이지 않으면 에러를 발생시킵니다.
          if (!response.ok) throw new Error("Network response was not ok");
          // 성공했다면, 응답 본문을 JSON 객체로 변환합니다.
          return response.json();
        })
        .then((data) => {
          // JSON 변환이 완료되면, 이 데이터를 사용하여 화면을 그리는 함수들을 호출합니다.
          // data는 서버에서 보낸 PageResponseDTO<ReviewDTO> 객체입니다.
          console.log("Server Response (Reviews):", data);
          displayReviews(data.dtoList); // 댓글 목록 그리기
          setupPagination(data);      // 페이지 번호 버튼 그리기
        })
        .catch((error) => {
          // fetch 과정에서 오류가 발생하면 콘솔에 에러를 출력하고, 사용자에게 메시지를 보여줍니다.
          console.error("Error fetching reviews:", error);
          reviewList.innerHTML = "<p>리뷰를 불러오는 데 실패했습니다.</p>";
        });
    }

    // 2. 받아온 댓글 목록 데이터를 사용하여 HTML 요소를 만들어 화면에 표시하는 함수
    function displayReviews(reviews) {
      // 기존에 있던 목록을 깨끗하게 비웁니다.
      reviewList.innerHTML = "";
      if (!reviews || reviews.length === 0) {
        reviewList.innerHTML = "<p>작성된 리뷰가 없습니다.</p>";
        return;
      }

      // 받아온 댓글 배열을 순회하면서 각 댓글에 대한 HTML을 생성합니다.
      reviews.forEach((review) => {
        const reviewItem = document.createElement("div");
        reviewItem.className = "review-item";
        // ReviewDTO의 필드(reviewContent, rating, createdAt 등)를 사용하여 HTML 구조를 만듭니다.
        reviewItem.innerHTML = `
            <div class="review-header">
                <div class="review-user-info">
                    <div class="review-user-name">user...</div> <!-- TODO: 사용자 이름 연결 필요 -->
                    <div class="review-date">${new Date(review.createdAt).toLocaleDateString()}</div>
                </div>
                <div class="review-rating">${renderStars(review.rating)}</div>
            </div>
            <div class="review-comment">${review.reviewContent}</div>
        `;
        // 완성된 HTML 요소를 reviewList div에 자식으로 추가합니다.
        reviewList.appendChild(reviewItem);
      });
    }

    // 3. PageResponseDTO 데이터를 기반으로 페이지네이션 UI(버튼)를 생성하는 함수
    function setupPagination(pageData) {
      paginationContainer.innerHTML = "";
      if (!pageData || pageData.totalCount === 0) return;

      // 페이지 번호 버튼을 만드는 헬퍼 함수
      const createPageButton = (text, pageNum) => {
        const btn = document.createElement("button");
        btn.innerText = text;
        btn.className = "pagination-button";
        // 버튼 클릭 시, 해당 페이지 번호로 getReviews 함수를 다시 호출하도록 이벤트를 설정합니다.
        btn.addEventListener("click", () => getReviews(pageNum, 5));
        return btn;
      };

      // '이전' 버튼 생성 (prev가 true일 때만)
      if (pageData.prev) {
        const prevBtn = createPageButton("이전", pageData.start - 1);
        paginationContainer.appendChild(prevBtn);
      }

      // 페이지 번호 버튼들 생성
      pageData.pageNumList.forEach((pageNum) => {
        const btn = createPageButton(pageNum, pageNum);
        // 현재 페이지와 번호가 같으면 'active' 클래스를 추가하여 강조합니다.
        if (pageNum === pageData.pageRequestDTO.page) {
          btn.classList.add("active");
        }
        paginationContainer.appendChild(btn);
      });

      // '다음' 버튼 생성 (next가 true일 때만)
      if (pageData.next) {
        const nextBtn = createPageButton("다음", pageData.end + 1);
        paginationContainer.appendChild(nextBtn);
      }
    }
    
    // 별점 아이콘을 생성하는 헬퍼 함수
    function renderStars(rating) {
        let starsHTML = '';
        for (let i = 1; i <= 5; i++) {
            starsHTML += `<span class="star ${i <= rating ? '' : 'empty'}">★</span>`;
        }
        return starsHTML;
    }

    // 4. 스크립트 최초 실행!
    // 페이지가 처음 로딩될 때, 1페이지의 댓글 목록을 가져오기 위해 함수를 호출합니다.
    getReviews();
  });
})();