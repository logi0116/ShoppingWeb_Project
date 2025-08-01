// ==================================================
// mypage.html 전용 스크립트
// ==================================================

// 페이지 로딩 시 사용자 정보 및 주문 내역 표시
document.addEventListener('DOMContentLoaded', () => {
    displayUserInfo();
    displayOrderHistory();
});

function displayUserInfo() {
    fetch('/api/members/me')
        .then(res => {
            if (!res.ok) throw new Error('사용자 정보를 불러올 수 없습니다.');
            return res.json();
        })
        .then(user => {
            const userInfo = document.getElementById('userInfo');
            if (!userInfo) return;
            userInfo.innerHTML = `
                <div class="user-detail"><strong>이름:</strong> ${user.userName}</div>
                <div class="user-detail"><strong>이메일:</strong> ${user.email}</div>
                <div class="user-detail"><strong>전화번호:</strong> ${user.phone}</div>
                <div class="user-detail"><strong>생일:</strong> ${new Date(user.birthDate).toLocaleDateString()}</div>
            `;
        })
        .catch(err => {
            console.error(err);
            alert('사용자 정보를 불러오는 데 실패했습니다.');
        });
}

// 주문 내역 표시 (mypage.html)
function displayOrderHistory() {
    fetch('/api/orders')
        .then(res => {
            if (!res.ok) throw new Error('주문 내역을 불러올 수 없습니다.');
            return res.json();
        })
        .then(orders => {
            const orderHistory = document.getElementById('orderHistory');
            if (!orderHistory) return;
            if (orders.length === 0) {
                orderHistory.innerHTML = '<p>주문 내역이 없습니다.</p>';
                return;
            }
            orderHistory.innerHTML = '';
            orders.reverse().forEach(order => {
                const orderItem = document.createElement('div');
                orderItem.className = 'order-item';
                const itemList = order.orderItems.map(item => `${item.productName} x ${item.quantity}`).join(', ');
                orderItem.innerHTML = `
                    <div class="order-date">주문일: ${new Date(order.orderDate).toLocaleDateString()}</div>
                    <div class="order-items">${itemList}</div>
                    <div class="order-total">총 금액: ${order.totalPrice.toLocaleString()}원</div>
                    <div class="order-address">배송지 : ${order.address},${order.addressDetail}</div>
                `;
                orderHistory.appendChild(orderItem);
            });
        })
        .catch(err => {
            console.error(err);
            alert('주문 내역을 불러오는 데 실패했습니다.');
        });
}
// 프로필 수정 (mypage.html)
function editProfile() {
    alert('프로필 수정 기능은 추후 구현 예정입니다.');
}