# 페이징 구현 방식 통일을 위한 팀 가이드

## 1. 왜 `Pageable` 방식을 선택했는가? (핵심 메시지)

우리 팀이 페이징 구현 방식으로 Spring Data JPA의 `Pageable`을 선택한 이유는 다음과 같습니다.

- **"한 번 만들고, 계속 재사용해요!" (높은 생산성)**

  > 공용 DTO를 한 번만 잘 설계해두면, 상품, 댓글, 검색 등 모든 페이징 기능에서 코드를 재사용하여 개발 속도를 높일 수 있습니다.

- **"서비스는 서비스답게!" (깔끔한 코드 구조)**

  > 복잡한 페이지 계산 로직을 DTO와 JPA에 위임함으로써, 서비스 계층은 순수한 비즈니스 로직에만 집중하여 코드의 가독성과 유지보수성을 향상시킬 수 있습니다.

- **"스프링에게 맡기세요!" (프레임워크 활용 극대화)**
  > 스프링 부트의 핵심 철학은 '자동화'와 '생산성'입니다. `Pageable`은 이 철학이 가장 잘 반영된 기능 중 하나로, 이를 활용하는 것이 프로젝트를 가장 효율적으로 개발하는 방법입니다.

---

## 2. 방식 변경에 따른 팀원별 작업 변화 비교

새로운 `Pageable` 방식을 도입함으로써, 기존 방식 대비 우리 팀원들의 작업이 어떻게 변하는지 각자의 입장에서 설명합니다.

### **To. 주현 & 찬우 (상품 엔티티, 관리자 페이지 담당)**

- **[요약]** 찬우님은 더 이상 페이징 로직을 복사할 필요 없이, 잘 만들어진 서비스를 호출만 하면 됩니다. 주현님은 DB 구조 설계에 더 집중할 수 있습니다.
- **(편해지는 점)** **찬우님**은 페이징 로직을 전혀 신경 쓸 필요 없이, 완성된 `ProductService`의 페이징 메소드를 **그대로 호출해서 사용하기만 하면 됩니다.** 코드 중복이 사라지고 유지보수가 매우 편해집니다.

### **To. 신우 (댓글 엔티티, 코드 병합 담당)**

- **[요약]** 복잡한 계산 로직을 복사할 필요 없이, 이미 만들어진 DTO와 구조를 재사용하여 훨씬 빠르고 안전하게 댓글 페이징을 구현할 수 있습니다.
- **(편해지는 점)** 검증된 구조를 따라가기만 하면 되므로, **훨씬 빠르고 안정적으로** 새로운 페이징 기능을 완성할 수 있습니다.

### **To. 지성 (프론트엔드, 주문/장바구니 담당)**

- **[요약]** 모든 페이징 API의 응답 구조가 100% 동일해지므로, 하나의 UI 컴포넌트를 만들어 모든 목록 페이지에서 재사용할 수 있어 생산성이 극대화됩니다.
- **(편해지는 점)** 앞으로 만들어질 **모든 페이징 API의 응답 구조가 100% 동일**합니다. 따라서 하나의 페이지네이션 UI 컴포넌트를 만들어두면, 어떤 목록 페이지에서든 **그대로 재사용**할 수 있습니다.

---

## 3. 그래서 우리는 앞으로 어떻게 해야 할까? (팀원별 Action Item)

제가 `PageRequestDTO`와 `PageResponseDTO`라는 공용 부품을 만든 이유는, 팀원 여러분들이 페이징 기능을 더 쉽고 일관되게 구현할 수 있도록 돕기 위함입니다. 앞으로 각자 페이징 관련 작업을 하실 때 아래 가이드를 참고해주세요.

### **To. 주현 & 찬우 (상품 / 관리자 페이지)**

- **핵심 키워드:** `QueryDSL`, `RepositoryCustom`
- **앞으로 수정/생성할 파일:**
  1.  `repository/search/ProductRepositoryCustom.java` (생성): 상품 검색 조건을 정의할 인터페이스.
  2.  `repository/search/ProductRepositoryCustomImpl.java` (생성): QueryDSL로 실제 검색+페이징 쿼리를 구현할 클래스.
  3.  `service/ProductService.java` (수정): `findAll` 대신 새로 만든 `search` 메소드를 호출하도록 변경.
- **DTO 활용법:**
  - `ProductService`의 목록 조회 메소드 파라미터로 `PageRequestDTO`를 받으세요.
  - 메소드의 최종 반환 값으로 `PageResponseDTO<ProductResponseDto>`를 사용하시면 됩니다.

### **To. 신우 (댓글)**

- **핵심 키워드:** `재사용`, `구조 참고`
- **앞으로 수정/생성할 파일:**
  1.  `repository/search/ReviewRepositoryCustom.java` (생성)
  2.  `repository/search/ReviewRepositoryCustomImpl.java` (생성)
  3.  `service/ReviewService.java` (수정)
- **DTO 활용법:**
  - 상품 페이징에서 사용된 `PageRequestDTO`와 `PageResponseDTO`를 **그대로 가져와서** 사용하시면 됩니다.
  - `PageResponseDTO`의 제네릭 타입만 `<ReviewResponseDto>`로 바꿔주면 모든 것이 동일하게 동작합니다.

### **To. 지성 (프론트엔드)**

- **핵심 키워드:** `API 요청/응답 규격`, `UI 컴포넌트 재사용`
- **참조할 파일:** `dto/PageRequestDTO.java`, `dto/PageResponseDTO.java`
- **DTO 활용법:**
  - **(요청 시)** 백엔드에 목록 데이터를 요청할 때, `axios.get('/products?page=1&size=10')` 와 같이 `PageRequestDTO`의 필드명을 쿼리 파라미터로 사용합니다.
  - **(응답 처리 시)** 서버로부터 받은 `PageResponseDTO`의 `dtoList`로 목록을 그리고, `pageNumList`, `prev`, `next` 등의 정보로 페이지네이션 UI를 만듭니다. 이 로직을 하나의 컴포넌트로 만들어두면 모든 페이지에서 재사용할 수 있습니다.


---

### **[2025-07-28] 페이징 공통 모듈 설계 (1단계) - PageRequestDTO 강화**

- **담당자:** (본인 이름)
- **관련 파일:** `com.busanit501.shoppingweb_project.dto.PageRequestDTO.java`

#### **1. 변경 내용**
- 기존 `PageRequestDTO`에 검색 기능을 처리하기 위한 `type`(검색 타입), `keyword`(검색어) 필드를 추가함.
- DTO가 스스로 유효한 검색 요청인지 판단할 수 있는 `hasKeyword()` 헬퍼 메소드를 추가함.

#### **2. 설계 의도 및 배경 (The "Why")**

- **(생각)** "페이징과 검색은 거의 항상 함께 사용되는데, 이 두 요청을 각각 다른 파라미터로 받으면 컨트롤러와 서비스가 너무 복잡해진다. 관련된 데이터와 기능은 하나의 객체로 묶어 관리하는 것이 **'높은 응집도'**를 가진 좋은 설계다. 따라서, `PageRequestDTO`를 '페이징과 검색'에 대한 모든 요청 정보를 담는 **표준 규격 상자**로 만들기로 결정했다."

- **(생각)** "서비스 로직에서 `if (keyword != null ...)` 같은 코드를 반복하는 것은 좋지 않다. 객체는 스스로의 상태를 책임져야 한다. `hasKeyword()` 메소드를 DTO 내부에 **캡슐화**함으로써, 서비스 계층의 코드를 간결하게 만들고 DTO의 역할을 더 명확히 했다. 이는 **'Tell, Don't Ask'** 원칙을 따른 것이다."

#### **3. 기대 효과 및 팀에 미치는 영향**

- **개발 생산성 향상:**
  - 팀원들은 앞으로 페이징/검색 기능이 필요할 때, 고민 없이 이 `PageRequestDTO`를 사용하기만 하면 된다.
  - 공통 모듈 사용으로 중복 코드가 줄어들고, 전체 개발 속도가 빨라진다.
- **유지보수 용이성:**
  - 페이징/검색 관련 정책이 변경될 경우, 이 DTO와 관련 서비스만 수정하면 되므로 수정 범위가 명확해진다.
- **팀원 간의 원활한 협업:**
  - "검색 기능은 `type`과 `keyword` 파라미터를 사용합니다" 와 같이 명확하고 표준화된 API 규격을 통해 프론트엔드 개발자와의 소통이 원활해진다.

---

### **[2025-07-28] 페이징 공통 모듈 설계 (2단계) - PageResponseDTO 설계**

- **담당자:** (본인 이름)
- **관련 파일:** `com.busanit501.shoppingweb_project.dto.PageResponseDTO.java`

#### **1. 설계 목표 (The "Why")**

- **(생각)** "서버가 클라이언트에게 페이징 결과를 돌려줄 때, 단순히 데이터 목록만 보내면 화면(View)에서 페이지네이션 UI(페이지 번호, 이전/다음 버튼 등)를 그릴 수 없다. 따라서, UI 렌더링에 필요한 모든 정보를 포함하는 **'표준화된 응답 규격'**을 만들기로 했다. 이것이 `PageResponseDTO`의 존재 이유다."
- **(생각)** "이 DTO는 제네릭(`<E>`)을 사용하여, `ProductResponseDto`, `ReviewResponseDto` 등 어떤 종류의 데이터 목록이든 담을 수 있는 **'범용 컨테이너'** 역할을 해야 한다. 이를 통해 코드 재사용성을 극대화하고, 모든 목록 페이지에서 일관된 방식으로 응답을 처리할 수 있다."

#### **2. 필드 및 메소드 상세 설명**

- **`private List<E> dtoList;`**
  - **역할:** 해당 페이지에 표시될 실제 데이터(DTO) 목록.
  - **상호작용:** 각 `Service` 계층(예: `ProductService`)에서 `Page<Entity>` 객체를 `List<DTO>`로 변환한 결과물이 담긴다.

- **`private long totalCount;`**
  - **역할:** 필터링된 전체 데이터의 개수.
  - **이유:** 전체 페이지 수를 계산하는 데 반드시 필요한 값이다.

- **`private PageRequestDTO pageRequestDTO;`**
  - **역할:** 클라이언트가 보냈던 원본 요청 정보를 그대로 담아둔다.
  - **이유:** View에서 현재 페이지 번호, 페이지 당 사이즈, 검색어 등의 정보를 알아야, 현재 페이지에 'active' 스타일을 적용하거나, 검색 상태를 유지한 채로 다른 페이지로 이동하는 링크를 생성할 수 있다.

- **`private int start, end;`**
  - **역할:** 화면에 표시될 페이지 번호 목록의 시작과 끝. (예: 1-10, 11-20)

- **`private boolean prev, next;`**
  - **역할:** '이전', '다음' 버튼의 활성화 여부.
  - **이유:** `start`가 1보다 클 때 `prev`가 `true`가 되고, `end` 이후에 더 많은 데이터가 있을 때 `next`가 `true`가 된다. View에서는 이 boolean 값으로 버튼을 조건부 렌더링한다.

#### **3. 핵심 로직 상세 분석: 생성자 내부 계산 로직**

- **`this.end = (int)(Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;`**
  - **목표:** 현재 페이지를 기준으로 UI에 표시될 **페이지네이션의 끝 번호**를 계산한다.
  - **예시:** 현재 페이지(`getPage()`)가 `7`이면, `7 / 10.0 = 0.7` -> `Math.ceil(0.7) = 1.0` -> `1 * 10 = 10`. 따라서 끝 번호는 `10`이 된다. 현재 페이지가 `13`이면, `13 / 10.0 = 1.3` -> `Math.ceil(1.3) = 2.0` -> `2 * 10 = 20`. 끝 번호는 `20`이 된다.

- **`int last = (int)(Math.ceil(totalCount / (double)pageRequestDTO.getSize()));`**
  - **목표:** 전체 데이터 개수(`totalCount`)와 페이지 당 사이즈(`getSize()`)를 기반으로, **데이터가 존재하는 실제 마지막 페이지 번호**를 계산한다.
  - **예시:** 전체 데이터가 `78`개이고, 사이즈가 `10`이면, `78 / 10.0 = 7.8` -> `Math.ceil(7.8) = 8.0`. 따라서 실제 마지막 페이지는 `8`이 된다.

- **`this.end = Math.min(end, last);`**
  - **목표:** 위에서 계산한 두 종류의 끝 번호를 비교하여, **UI 끝 번호를 보정**한다.
  - **이유(매우 중요):** 만약 실제 마지막 페이지가 `8`인데, UI 끝 번호가 `10`으로 계산되었다면, 화면에 `9`, `10`이라는 유령 페이지 버튼이 표시되면 안 된다. 따라서 둘 중 더 작은 값(`8`)을 최종 `end` 값으로 선택하여, 항상 유효한 페이지 번호만 표시되도록 보장한다.

---


---

### **[2025-07-28] 페이징 공통 모듈 설계 (3단계) - Repository 확장**

- **담당자:** (본인 이름)
- **관련 파일:** `...repository.search.ProductRepositoryCustom.java`

#### **1. `ProductRepositoryCustom` 인터페이스를 분리한 이유 (The "Why")**

- **(생각)** "Spring Data JPA가 기본으로 제공하는 `JpaRepository`는 `findById`, `save` 같은 간단한 CRUD 메소드에 최적화되어 있다. 하지만 우리는 '검색어'와 '검색 타입'에 따라 쿼리가 계속 바뀌는 **동적 쿼리**가 필요하다. 이 복잡한 로직을 기존 `ProductRepository`에 다 섞어버리면, 역할이 불분명해지고 코드가 지저분해진다."
- **(생각)** "그래서 **'역할과 책임의 분리(Separation of Concerns)'** 원칙에 따라, 기본 CRUD는 `ProductRepository`에 그대로 두고, 우리가 직접 만들 복잡한 동적 쿼리는 `ProductRepositoryCustom`이라는 별도의 '설계도'에 정의하기로 했다. 이렇게 하면 관리가 훨씬 쉬워진다."

#### **2. Spring Data JPA와의 상호작용 방식**

- **(핵심 원리)** Spring Data JPA는 아주 똑똑해서, 우리가 `[기존 리포지토리 이름]Custom` 과 `[기존 리포지토리 이름]CustomImpl` 이라는 이름 규칙만 지켜주면, **자동으로 두 개의 인터페이스와 구현체를 하나로 합쳐준다.**
- **(동작 순서)**
  1. `ProductRepository`가 `ProductRepositoryCustom`을 `extends` (상속)한다.
  2. 우리는 `ProductRepositoryCustomImpl` 클래스에 `@Repository` 어노테이션을 붙여서 Bean으로 등록한다.
  3. Spring이 실행될 때, `ProductRepository`의 프록시(Proxy) 객체를 만들면서, `ProductRepositoryCustomImpl`에 구현된 메소드(`search()`)를 자동으로 합쳐준다.
- **(결론)** 상품 관련 코드를 담당하는 **주현씨와 찬우씨**는 `ProductRepository` 하나만 주입(`@Autowired`)받으면, `findById` 같은 기본 메소드와 우리가 만들 `search()` 메소드를 모두 사용할 수 있게 된다. 이것이 바로 프레임워크를 올바르게 활용하는 방법이다.

---


---

### **[2025-07-28] 페이징 공통 모듈 설계 (4단계) - Custom Repository 구현**

- **담당자:** (본인 이름)
- **관련 파일:** `...repository.search.ProductRepositoryCustomImpl.java`

#### **1. `ProductRepositoryCustomImpl`의 역할 (The "Why")**

- **(생각)** "이 클래스는 `ProductRepositoryCustom`이라는 '설계도'를 보고 실제 '건물'을 짓는 곳이다. 즉, Querydsl을 사용하여 복잡한 동적 쿼리를 실제로 구현하는 책임은 오직 이 클래스만이 가진다."
- **(생각)** "특히 `PageRequestDTO`로부터 `type`과 `keyword`를 받아, 어떤 조건으로 검색할지를 `if`문으로 분기 처리하는 로직이 여기에 들어간다. 이것이 바로 **'동적 쿼리'**의 핵심이다. 예를 들어, `type`이 't'이면 상품명(`productName`)에서만 검색하고, 'c'이면 상품 설명(`productTag`)에서 검색하도록 만들 수 있다."

#### **2. 핵심 코드 분석**

- **`private final JPAQueryFactory queryFactory;`**
  - **역할:** Querydsl 쿼리를 생성하고 실행하는 핵심 팩토리 클래스.
  - **이유:** 생성자에서 `EntityManager`를 주입받아 초기화하며, 이 `queryFactory`를 통해 모든 DB 조작이 이루어진다.

- **`QProduct product = QProduct.product;`**
  - **역할:** Querydsl이 `Product` 엔티티를 기반으로 자동 생성한 Q-Type 클래스.
  - **이유:** `product.productName.contains(keyword)` 와 같이, 문자열이 아닌 자바 코드로 컬럼에 접근할 수 있게 해준다. 이는 컴파일 시점에 오타나 문법 오류를 잡아낼 수 있어 **타입 안정성(Type Safety)**을 획기적으로 높여준다.

- **`BooleanBuilder booleanBuilder = new BooleanBuilder();`**
  - **역할:** `where` 절에 들어갈 여러 검색 조건을 담는 컨테이너.
  - **이유:** `if` 문을 통해 특정 조건이 참일 때만 `booleanBuilder.and(...)` 또는 `booleanBuilder.or(...)`를 사용하여 조건을 동적으로 추가할 수 있다. 모든 조건이 조립된 후, `query.where(booleanBuilder)` 한 줄로 `where` 절을 완성할 수 있어 코드가 매우 깔끔해진다.

- **`query.offset(pageable.getOffset()).limit(pageable.getPageSize());`**
  - **역할:** 페이징 처리의 핵심.
  - **상호작용:** `PageRequestDTO`로부터 만들어진 `Pageable` 객체의 정보를 사용하여, "몇 번째 데이터부터 몇 개를 가져올지"를 DB에 정확히 알려준다.

- **`new PageImpl<>(content, pageable, total);`**
  - **역할:** 조회된 데이터 목록(`content`), 원본 페이징 정보(`pageable`), 그리고 전체 개수(`total`)를 합쳐 Spring Data JPA가 사용하는 `Page` 객체로 만들어 반환한다.
  - **상호작용:** 이 `Page<Product>` 객체는 `Service` 계층으로 전달되어, 최종적으로 `PageResponseDTO`를 만드는 데 사용된다.

---


---

### **[2025-07-29] 프론트엔드 페이징 구현 - review-paging.js 분석**

- **담당자:** (본인 이름)
- **관련 파일:** `static/js/review-paging.js`, `templates/product-detail.html`

#### **1. 구현 목표 (The "Why")**

- **(생각)** "백엔드에서 페이징된 댓글 데이터를 제공하는 API(`api/reviews/{productId}`)를 완성했으니, 이제 프론트엔드에서 이 API를 호출하여 실제 화면에 댓글 목록과 페이지 번호 버튼들을 그려줘야 한다. 이 모든 동적인 화면 처리 로직을 `product-detail.html`에 직접 넣으면 코드가 지저분해지니, `review-paging.js`라는 별도의 파일에 캡슐화하여 역할을 명확히 분리하기로 했다."

#### **2. 핵심 동작 흐름 (AJAX)**

1.  **페이지 로딩:** 사용자가 `product-detail.html` 페이지에 접속합니다.
2.  **스크립트 실행:** 페이지 로딩이 완료되면, `review-paging.js`가 실행됩니다.
3.  **API 호출 (1차):** `getReviews()` 함수가 자동으로 호출되어, 백엔드의 `api/reviews/{productId}?page=1&size=5` API로 첫 페이지 댓글 데이터를 요청합니다. (AJAX 통신)
4.  **데이터 수신:** 백엔드는 `PageResponseDTO<ReviewDTO>` 객체를 JSON 데이터로 변환하여 응답합니다.
5.  **화면 렌더링:** Javascript는 받아온 JSON 데이터를 사용하여 `displayReviews()` 함수로 댓글 목록 HTML을 만들고, `setupPagination()` 함수로 페이지 번호 버튼 HTML을 만들어 화면에 동적으로 그려줍니다.
6.  **페이지 이동 (2차 이후):** 사용자가 페이지 번호 버튼(예: '2'번)을 클릭하면, `getReviews(2, 5)` 함수가 다시 호출되어 2페이지 데이터를 요청하고, 4~5번 과정을 반복합니다. 이 과정에서는 페이지 전체가 새로고침되지 않고, 댓글 목록과 페이징 영역만 부드럽게 변경됩니다.

#### **3. 주요 함수 분석**

- **`getReviews(page, size)`:**
  - **역할:** 백엔드에 댓글 데이터를 요청하는 **'AJAX 통신'**의 핵심입니다. `fetch` API를 사용하여 비동기 GET 요청을 보냅니다.
  - **상호작용:** `ReviewController`의 `@GetMapping("/{productId}")` 메소드와 직접 통신합니다.

- **`displayReviews(reviews)`:**
  - **역할:** 순수한 댓글 데이터 배열(`reviews`)을 받아서, `for`문을 돌며 동적으로 HTML 태그(`<div>`, `<span>` 등)를 생성하고, 최종적으로 화면에 표시하는 **'렌더링 엔진'**입니다.

- **`setupPagination(pageData)`:**
  - **역할:** `PageResponseDTO` 객체(`pageData`)를 통째로 받아서, `pageNumList`, `prev`, `next` 등의 정보를 조합하여 페이지네이션 UI(버튼 목록)를 동적으로 생성하는 **'UI 빌더'**입니다.

---
