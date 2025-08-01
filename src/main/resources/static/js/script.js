// const products = [
//     {id: 1, name: '클래식 청바지', category: '바지', price: 89000, image: '👖'},
//     {id: 2, name: '슬림핏 치노', category: '바지', price: 65000, image: '👖'},
//     {id: 3, name: '운동화', category: '신발', price: 120000, image: '👟'},
//     {id: 4, name: '구두', category: '신발', price: 150000, image: '👞'},
//     {id: 5, name: '면 티셔츠', category: '상의', price: 35000, image: '👕'},
//     {id: 6, name: '셔츠', category: '상의', price: 55000, image: '👔'},
//     {id: 7, name: '겨울 코트', category: '아우터', price: 200000, image: '🧥'},
//     {id: 8, name: '후드집업', category: '아우터', price: 85000, image: '🧥'},
//     {id: 9, name: '스키니 진', category: '바지', price: 75000, image: '👖'},
//     {id: 10, name: '하이탑 스니커즈', category: '신발', price: 95000, image: '👟'},
//     {id: 11, name: '폴로 셔츠', category: '상의', price: 45000, image: '👕'},
//     {id: 12, name: '바람막이', category: '아우터', price: 110000, image: '🧥'}
// ];

// 전역 변수 (모든 페이지에서 사용)
let cart = [];          // 장바구니 아이템 (localStorage에서 로드)
let reviews = [];       // 상품 리뷰 (localStorage에서 로드)
let selectedRating = 5; // 리뷰 작성 시 선택된 별점 (product-detail.html에서 사용)

// 페이지 로드 시 초기화 함수 (모든 HTML 페이지의 <script> 태그 내부에 포함)
document.addEventListener('DOMContentLoaded', function () {
    // initializePage 함수가 현재 페이지에 존재하는 경우에만 호출합니다.
    if (typeof initializePage === 'function') {
        initializePage();
    }
});


// 사용자 데이터 로드 (모든 페이지에서 사용)
function loadUserData() {
    const userData = localStorage.getItem('currentUser');
    if (userData) {
        currentUser = JSON.parse(userData);
    }
}

// 장바구니 데이터 로드 (모든 페이지에서 사용)
function loadCartData() {
    const cartData = localStorage.getItem('cart');
    if (cartData) {
        cart = JSON.parse(cartData);
    }
}

// 리뷰 데이터 로드 (product-detail.html에서 주로 사용)
function loadReviewData() {
    const reviewData = localStorage.getItem('reviews');
    if (reviewData) {
        reviews = JSON.parse(reviewData);
    }
}

// UI 업데이트 (모든 페이지에서 사용)
function updateUI() {
    const loginBtn = document.getElementById('loginBtn');
    const logoutBtn = document.getElementById('logoutBtn');
    const cartCount = document.getElementById('cartCount');

    if (loginBtn && logoutBtn) {
        if (currentUser) {
            loginBtn.style.display = 'none';
            logoutBtn.style.display = 'inline-block';
        } else {
            loginBtn.style.display = 'inline-block';
            logoutBtn.style.display = 'none';
        }
    }
    if (cartCount) {
        cartCount.textContent = cart.reduce((total, item) => total + item.quantity, 0);
    }
}
function goToLogin() {
    window.location.href = '/login';
}

function goToSignup() {
    window.location.href = '/signup';
}

function goToCart() {
    window.location.href = '/cart';
}

function goToMyPage() {
    window.location.href = '/mypage';
}

function logout() {
  window.location.href = "/logout";
}
// ====================================================================================================
// home.html (메인 페이지) 관련 JavaScript 함수
// ====================================================================================================




// ====================================================================================================
// product-detail.html (상품 상세 페이지) 관련 JavaScript 함수
// ====================================================================================================




// ====================================================================================================
// cart.html (장바구니 페이지) 관련 JavaScript 함수
// ====================================================================================================




// ====================================================================================================
// signup.html (회원가입 페이지) 관련 JavaScript 함수
// ====================================================================================================

// 주소 검색 API
function loadDaumPostcodeScript(callback) {
    if (window.daum && window.daum.Postcode) {
        callback();
        return;
    }

    const script = document.createElement('script');
    script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    script.onload = () => {
        console.log('다음 주소 API 로드 완료');
        callback();
    };
    script.onerror = () => {
        alert('주소 검색 스크립트를 불러오는 데 실패했습니다.');
    };
    document.head.appendChild(script);
}

function execDaumPostcode() {
    loadDaumPostcodeScript(() => {
        new daum.Postcode({
            oncomplete: function (data) {
                document.getElementById('zipcode').value = data.zonecode;
                document.getElementById('addressId').value = data.roadAddress;
                document.getElementById('addressLine').focus();
            }
        }).open();
    });
}

// 아이디 중복 확인
function checkDuplicateId() {
    const memberId = document.getElementById('memberId').value;
    const resultDiv = document.getElementById('idCheckResult');

    if (!memberId) {
        resultDiv.textContent = '아이디를 입력하세요';
        resultDiv.style.color = 'red';
        return;
    }

    fetch(`/api/check-id?memberId=${encodeURIComponent(memberId)}`)
        .then(response => response.json())
        .then(data => {
            if (data.duplicate) {
                resultDiv.textContent = '이미 사용 중인 아이디입니다.';
                resultDiv.style.color = 'red';
            } else {
                resultDiv.textContent = '사용 가능한 아이디입니다.';
                resultDiv.style.color = 'green';
            }
        })
        .catch(() => {
            resultDiv.textContent = '서버 오류';
            resultDiv.style.color = 'red';
        });
}

// 비밀번호 확인 및 폼 제출 검증
// signup.html 페이지에만 존재하는 요소들을 위한 스크립트
document.addEventListener('DOMContentLoaded', () => {
    // signupForm이 있는 페이지에서만 아래 코드를 실행합니다.
    const form = document.getElementById('signupForm');
    if (form) {
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const passwordHelp = document.getElementById('passwordHelp');

        if (confirmPassword) {
            confirmPassword.addEventListener('input', function () {
                if (password.value !== confirmPassword.value) {
                    passwordHelp.style.display = 'block';
                } else {
                    passwordHelp.style.display = 'none';
                }
            });
        }

        form.addEventListener('submit', function (e) {
            if (password.value !== confirmPassword.value) {
                e.preventDefault();
                alert('비밀번호가 일치하지 않습니다.');
                confirmPassword.focus();
            }
        });
    }
});


// ====================================================================================================
// login.html (로그인 페이지) 관련 JavaScript 함수
// ====================================================================================================



// ====================================================================================================
// mypage.html (마이페이지) 관련 JavaScript 함수
// ====================================================================================================

