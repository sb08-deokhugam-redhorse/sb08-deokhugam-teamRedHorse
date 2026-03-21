## [🐴 TEAM RedHorse](https://ohgiraffers.notion.site/1-306649136c1180579ecccf1f7ba8cfdc?source=copy_link) [![codecov](https://codecov.io/gh/sb08-deokhugam-redhorse/sb08-deokhugam-teamRedHorse/graph/badge.svg?token=OZV30ISBOG)](https://codecov.io/gh/sb08-deokhugam-redhorse/sb08-deokhugam-teamRedHorse)
| <img src="https://github.com/castle-bird.png" width="100" style="border-radius:50%"/> | <img src="https://github.com/xxzeroeight.png" width="100" style="border-radius:50%"/> | <img src="https://github.com/parkhamin.png" width="100" style="border-radius:50%"/> | <img src="https://github.com/yeeun000.png" width="100" style="border-radius:50%"/> | <img src="https://github.com/dev-jin8612.png" width="100" style="border-radius:50%"/> |
|:-------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------:|
|                                        **박성조**                                        |                                        **김경태**                                        |                                       **박하민**                                       |                                      **이예은**                                       |                                        **황진서**                                        |
|                    [@castle-bird](https://github.com/castle-bird)                     |                    [@xxzeroeight](https://github.com/xxzeroeight)                     |                     [@parkhamin](https://github.com/parkhamin)                      |                      [@yeeun000](https://github.com/yeeun000)                      |                    [@dev-jin8612](https://github.com/dev-jin8612)                     |
|                                        팀장, 서기                                         |                                       인프라/형상 관리                                       |                                         PM                                          |                                     데이터베이스 관리                                      |                                     인프라/형상 관리, 발표                                     |
|                                        사용자 관리                                         |                                         도서 관리                                         |                                        댓글 관리                                        |                                       리뷰 관리                                        |                                      대시보드, 알림 관리                                      |

<br/>


## 🌐 배포 사이트
[![deokhugam](https://img.shields.io/badge/deokhugam-0288D1?style=for-the-badge&logo=bookstack&logoColor=white)](https://deokhugam.xyz)

<br/>

## 🏝️ AWS Architecture
<img width="970" height="434" alt="Image" src="https://github.com/user-attachments/assets/67b2cb22-2b27-4a7b-aff7-c7660e456847" />

<br/>
<br/>

## 📌 프로젝트 개요
### 프로젝트 명
덕후감(Deokhugam)

### 프로젝트 소개
덕후감은 책 읽는 즐거움을 공유하고, 지식과 감상을 나누는 책 덕후들의 커뮤니티 서비스입니다.  
도서 이미지 OCR 및 ISBN 매칭 서비스를 제공합니다.

### 프로젝트 정보
- **기간**: 2026.02.27 ~ 2026.03.23
- **인원**: 5명
- **역할**: 도서 커뮤니티 서비스의 Backend 개발

<br/>

## 🚀 주요 기능
**사용자 관리**
- 회원가입 및 로그인/로그아웃 기능
- 사용자 프로필 조회 및 수정
- 비밀번호 변경 및 계정 관리

**도서 관리**
- ISBN 기반 도서 등록/조회/수정/삭제
- 이미지 OCR로 ISBN 자동 추출
- Resilience4j 서킷브레이커 + AWS Textract 자동 폴백
- Naver Book API 연동으로 ISBN → 도서 정보 자동 조회
- S3 썸네일, 로그 파일 자동 업로드

**리뷰 관리**
- 리뷰 등록
- 리뷰 수정
- 리뷰 삭제
- 리뷰 목록 & 상세 조회
- 리뷰 좋아요

**댓글 관리**
- 댓글 등록
- 댓글 수정
- 댓글 논리 & 물리 삭제
- 댓글 단건 & 목록 조회

**대시보드 관리**
- 대시보드 자동 순위 계산

**알림 관리**
- 알림 생성
- 자동 삭제
- 대시보드 자동 순위 계산

<br/>

## 🛠️ 기술 스택
### Language
![Java](https://img.shields.io/badge/Java_17-007396?style=flat&logo=java&logoColor=white)

### Backend
![Spring](https://img.shields.io/badge/Spring_6.2.16-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.11-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA_3.5.11-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Actuator](https://img.shields.io/badge/Spring_Actuator_3.5.11-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Batch](https://img.shields.io/badge/Spring_Batch_3.5.11-6DB33F?style=flat&logo=spring&logoColor=white)

### Database
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_17.7-4169E1?style=flat&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2_2.3.232-0000BB?style=flat&logo=h2&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway_11.7.2-CC0200?style=flat&logo=flyway&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL_5.0.0-0085CA?style=flat&logoColor=white)

### Cloud & Storage
![AWS S3](https://img.shields.io/badge/AWS_S3_SDK_2.41.24-FF9900?style=flat&logo=amazons3&logoColor=white)

### Deploy
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=githubactions&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-FF9900?style=flat&logo=amazonaws&logoColor=white)

### Docs
![Swagger](https://img.shields.io/badge/Swagger_2.8.16-85EA2D?style=flat&logo=swagger&logoColor=black)

### Utilities
![Lombok](https://img.shields.io/badge/Lombok_1.18.42-CA0000?style=flat&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct_1.6.3-D22128?style=flat&logoColor=white)
![dotenv](https://img.shields.io/badge/dotenv_3.0.0-ECD53F?style=flat&logo=dotenv&logoColor=black)

### Test
![JUnit5](https://img.shields.io/badge/JUnit_5.12.2-25A162?style=flat&logo=junit5&logoColor=white)

<br/>

## 🔍 구현 기능 상세
### **박성조 (사용자 관리)**
![사용자 관리](https://github.com/user-attachments/assets/2a63250f-79d1-45e6-89c4-4c66ddc80de3)

<details>
<summary>사용자 인증</summary>

- 인증 메커니즘: 모든 API 요청 시 헤더에 `Deokhugam-Request-User-ID` (`UUID` 형식)를 포함해야 합니다.
- 인가 정책: 해당 헤더가 존재하고 유효한 사용자인 경우에만 API 접근이 허용되며, 인증되지 않은 요청은 401 `Unauthorized` 또는 403 `Forbidden` 처리를 원칙으로 합니다.
- 식별 방식: 서버는 전달받은 `UUID`를 통해 요청자를 식별하고 비즈니스 로직을 수행합니다.

</details>

<details>
<summary>사용자 삭제 정책</summary>

- 논리 삭제 (`Soft Delete`) 우선: 사용자 탈퇴 시 즉시 데이터를 삭제하지 않고, `is_deleted` 상태를 변경하고 `deleted_at` 시간을 기록하는 논리 삭제를 기본으로 합니다.
- 물리 삭제 (`Hard Delete`) 조건:
  - 논리 삭제 후 24시간(1일)이 경과한 경우 배치 작업을 통해 자동 삭제됩니다.
  - 사용자가 명시적인 물리 삭제 API(`/api/users/{userId}/hard`)를 호출한 경우 즉시 삭제됩니다.
- 연관 데이터 처리: 물리 삭제 시 해당 사용자와 연관된 리뷰, 댓글, 활동 점수 등 모든 데이터는 무결성을 위해 함께 삭제(`Cascade Delete`)되어야 합니다.

</details>

<details>
<summary>삭제 상세 기능</summary>

- 실시간 논리 삭제
    - 사용자의 상태를 '삭제됨'으로 변경하고 접근을 차단하되, 오동작이나 변심에 대비해 1일간 데이터를 유지합니다.
- 스케줄링 기반 자동 물리 삭제
    - 매일 정해진 시간(예: 오전 2시)에 실행되는 배치 작업을 통해, 삭제된 지 1일이 지난 사용자의 정보를 DB에서 완전히 제거합니다.
- 명시적 즉시 물리 삭제
  - 관리자 권한 또는 특정 요구사항에 의해 호출되며, 유예 기간 없이 사용자와 관련된 모든 데이터를 즉시 영구 삭제합니다.

</details>


<br/>

### **김경태 (도서 관리)**
![도서 관리](https://github.com/user-attachments/assets/a6988659-adfe-438a-b52b-af41d02bf853)

<details>
<summary>도서 CRUD</summary>

- 도서 등록/단건 조회/수정/논리/물리 삭제 엔드포인트 구현
- ISBN 중복 불가, 생성 후 수정 불가 비즈니스 규칙 적용
- 논리 삭제 자동 필터링

</details>

<details>
<summary>도서 목록 조회 및 커서 페이지네이션</summary>

- 키워드 검색 (제목/저자/ISBN) + 단일 정렬 기준 지원
- QueryDSL로 동적 쿼리 구현
- 커서 기반 페이지네이션
- 도서 단건 조회 Caffeine 캐싱 적용

</details>

<details>
<summary>외부 API 연동 (Naver Book API + OCR)</summary>

- Naver API 응답 Caffeine 캐싱 적용
- OCR Space API로 책 표지 이미지에서 ISBN 추출
- ISBN 파싱: 정규식 + OCR 오인식 보정 + 체크섬 검증
- 이미지 사이즈별 응답시간 측정 기반 타임아웃 설정

</details>

<details>
<summary>OCR Textract 폴백 및 안정성 개선</summary>

- OCR Space 서버 장애 시 Resilience4j CircuitBreaker + Retry로 Textract 자동 폴백
- OCR Space ISBN 인식 실패 시 Textract 직접 호출
- Textract Text 방식으로 ISBN 직접 추출
- 이미지 사이즈별 응답시간 측정 기반 타임아웃 및 재시도 간격 설정

</details>

<details>
<summary>S3 스토리지 및 로그 관리</summary>

- 썸네일 S3 업로드 후 조회 시 presigned URL 자동 변환
- 매일 01:00 전날 로그 파일 S3 업로드
- 업로드 실패 시 경로 저장 후 다음 스케줄러 실행 시 재시도

</details>

<br/>

### **박하민 (댓글 관리)**
![댓글 관리](https://github.com/user-attachments/assets/ecc5397e-5ddf-47b6-b5b1-c7d134b09f13)

<details>
<summary>댓글 등록</summary>

- 리뷰 별로 댓글 등록 가능
- 댓글 등록할 경우 제약조건을 통해 사용자, 리뷰, 댓글의 내용을 검증

</details>

<details>
<summary>댓글 수정</summary>

- 본인이 작성한 댓글만 수정 가능
- 내용이 비어있거나 수정하기 전 댓글의 내용과 같을 경우 수정 불가

</details>

<details>
<summary>댓글 논리 삭제</summary>

- 본인이 작성한 댓글만 삭제 가능
- DB에는 관련 데이터가 남아있음
- 삭제할 댓글을 조회 시 findByIdAndDeletedIsNull 쿼리 메소드를 사용해 논리 삭제가 되지 않은 댓글을 조회하여 해당 댓글을 논리 삭제 처리

</details>

<details>
<summary>댓글 물리 삭제</summary>

- 본인이 작성한 댓글만 삭제 가능
- 삭제할 댓글을 조회 시 findById 쿼리 메소드를 사용해 논리 삭제가 된 댓글까지도 조회하여 해당 댓글을 물리 삭제 처리

</details>

<details>
<summary>댓글 단건 조회</summary>

- 댓글 단건 조회 시 caffeine 캐시 적용 하여 성능 향상
- 댓글 단건 조회 시 논리 삭제 처리된 경우 조회되지 않도록 함

</details>

<details>
<summary>댓글 목록 조회</summary>

- 시간 순 정렬 및 페이지네이션 구현
- 댓글 목록 조회 시 QueryDsl을 이용한 쿼리 고도화 작업
- 작성자를 fetch Join함으로써 N+1 문제 방지
- 생성 시간(createdAt) 중복 발생 시 정렬 보장을 위한 ID 기반 보조 커서(Tie-breaker) 도입
- 댓글 목록 조회 시 삭제 처리 된 댓글 제외하고 조회

</details>

<br/>

### **이예은 (리뷰 관리)**
![리뷰 관리](https://github.com/user-attachments/assets/df1c7c2f-7774-4633-b5a1-9e79657eb1b4)

<details>
<summary>리뷰 등록</summary>

- 사용자는 도서별 1개의 리뷰만 등록 가능
- 입력값 검증을 통해 평점 범위를 1~5점으로 제한

</details>

<details>
<summary>리뷰 수정</summary>

- 본인이 작성한 리뷰만 수정 가능
- 내용이 비었을 경우 수정 불가
- 동시성 충돌을 방지하기 위해 PESSIMISTIC_WRITE 락 사용

</details>

<details>
<summary>논리 삭제</summary>

- 관련된 정보가 유지되도록 삭제
- deleted_at의 값이 null인 경우에만 삭제 가능
- 데이터 정합성을 위해 PESSIMISTIC_WRITE 락 사용

</details>

<details>
<summary>물리 삭제</summary>

- 관련된 정보 모두 삭제
- deleted_at의 값이 null인 경우에만 삭제 가능

</details>

<details>
<summary>리뷰 목록</summary>

- 리뷰 작성자 닉네임, 내용, 도서 제목은 부분 일치 검색 가능
- 작성자 ID, 도서 ID 는 완전 일치 검색 가능
- 시간 또는 평점으로 정렬 및 페이지네이션
- 목록에 좋아요 수, 댓글 수 포함
- deleted_at이 null인 데이터만 조회
- QueryDSL 사용으로 동적 쿼리 구성

</details>

<details>
<summary>리뷰 상세 조회</summary>

- 단건 조회 시 좋아요 수, 댓글 수 포함
- 캐시 적용으로 DB 조회 횟수 감소하여 성능 개선
- Lazy 로딩 문제를 방지하기 위해 EntityGraph 사용

</details>

<details>
<summary>리뷰 좋아요</summary>

- 사용자는 리뷰별 1개의 좋아요만 등록 가능
- (review_id, user_id) 복합 유니크 키를 사용
- 리뷰 좋아요가 없을 경우 생성, 이미 존재할 경우 deleted_at에 취소 시간 입력
- 리뷰 엔티티에 likeCount 필드를 통해 좋아요가 등록/ 취소 될 때마다 값 갱신

</details>

<br/>

### **황진서 (대시보드, 알림 관리)**
![대시보드](https://github.com/user-attachments/assets/b5187c48-7278-44ae-9131-977180590126)
![알림](https://github.com/user-attachments/assets/5f6d5091-cb38-4a66-a821-77200e59e329)

<details>
<summary>알림</summary>

- 본인이 작성한 리뷰에 댓글, 좋아요 기재시 알림생성
- 본인이 작성한 리뷰나 본인이 파워유저 10위 이내 등극시 알림생성
- 알림을 클릭하면 확인(단일 조회)을 실행
- 모두 읽음을 클릭하면 전체를 확인상태(전체 조회)로 만듬
- 매일 배치를 돌려 1주일이 지난 알림을 자동으로 삭제

</details>

<details>
<summary>대시보드</summary>

- 매일 스프링 배치로 일/주/월/전체 기간별 순위를 생성
- 도서는 (해당 기간의 리뷰수 * 0.4) + (해당 기간의 평점 평균 * 0.6)으로 계산
- 리뷰는 (해당 기간의 좋아요 수 * 0.3) + (해당 기간의 댓글 수 * 0.7)으로 계산
- 유저는 (해당 기간의 작성한 리뷰의 인기 점수 * 0.5) + (참여한 좋아요 수 * 0.2) + (참여한 댓글 수 * 0.3)으로 계산

</details>

<br/>

## 📚 API 문서
[API 문서](https://deokhugam.xyz/swagger-ui/index.html)

<br/>

## 📊 프로젝트 구조
```markdown
.
├── .github/
│   ├── ISSUE_TEMPLATE
│   ├── workflows
│   └── PULL_REQUEST_TEMPLATE.md
└── src/
    ├── main/
    │   ├── java/com/redhorse/deokhugam/
    │   │   ├── domain/
    │   │   │   ├── alarm/
    │   │   │   │   ├── controller/
    │   │   │   │   │   └── api
    │   │   │   │   ├── dto
    │   │   │   │   ├── entity
    │   │   │   │   ├── exception
    │   │   │   │   ├── mapper
    │   │   │   │   ├── repository
    │   │   │   │   └── service
    │   │   │   ├── book/
    │   │   │   ├── comment/
    │   │   │   ├── dashboard/
    │   │   │   ├── review/
    │   │   │   └── user/
    │   │   ├── global/
    │   │   │   ├── config
    │   │   │   ├── entity
    │   │   │   ├── exception
    │   │   │   └── batch/
    │   │   │       ├── batchconfig
    │   │   │       └── repository
    │   │   └── infra/
    │   │       ├── naver/
    │   │       ├── ocr/
    │   │       └── s3/
    │   │           └── scheduler
    │   └── resources/
    │       ├── db/
    │       │   ├── migration
    │       │   └── seed
    │       └── static/
    └── test/
        ├── java/com/redhorse/deokhugam/
        │   ├── batch
        │   ├── domain/
        │   │   ├── comment/
        │   │   │   ├── controller
        │   │   │   ├── repository
        │   │   │   ├── service
        │   │   │   └── CommentIntegrationTest
        │   │   ├── alarm/
        │   │   ├── book/
        │   │   ├── dashboard/
        │   │   ├── review/
        │   │   └── user/
        │   └── infra/
        │       ├── naver
        │       ├── ocr
        │       └── s3
        └── resources
```