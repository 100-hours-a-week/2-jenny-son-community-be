# 아무 말 대잔치 커뮤니티 백엔드 프로젝트

## 1. 프로젝트 소개

Spring Boot 기반의 커뮤니티 백엔드 애플리케이션입니다.

주요 기능으로는 유저 인증(JWT 기반), 게시글/댓글 작성, 좋아요, 이미지 업로드가 있습니다.  
 
[(📂 프론트엔드 프로젝트 리포지토리)](https://github.com/100-hours-a-week/2-jenny-son-community-fe)

## 2. 사용 기술

| 구분 | 기술 |
| --- | --- |
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 3.4.3 |
| 데이터베이스 | MariaDB 10.4.28 |
| ORM | Spring Data JPA, Hibernate |
| 보안 | Spring Security, JJWT 0.12.6 |
| 빌드 도구 | Gradle |

## 3. 설계

### 1) API 설계

[🔗 API 문서 바로가기](https://www.notion.so/1bb258f8f61980fab751f83b11554fc6?pvs=21)

<details>
  <summary>API 문서 사진</summary>
  <div markdown="1">
    <ul>
      <img src="https://github.com/user-attachments/assets/a4be46cc-6abb-4e8e-85bb-e7811b2e4ebf" width=70%>
      <img src="https://github.com/user-attachments/assets/ca50e8cc-8a9d-4b36-b395-fc5759509419" width=70%>
      <img src="https://github.com/user-attachments/assets/dfc0ba37-5765-43dd-93cf-06f0d3992ece" width=70%>
    </ul>
  </div>
</details>

### 2) ERD

[🔗 ERD 바로가기](https://www.erdcloud.com/d/3R3zuaqirbN94gnqp)

<details>
  <summary>ERD 사진</summary>
  <div markdown="1">
    <ul>
      <img src="https://github.com/user-attachments/assets/8703516c-9dee-4a0e-b834-29e2a2f03533" width=70%>
    </ul>
  </div>
</details>


### 3) 프로젝트 구조

```java
katebu_community.community_be 
├── config                       // 보안 설정(SecurityConfig) 및 글로벌 설정 
├── controller                   // REST API 엔드포인트 담당 
├── domain                       // 엔티티(Entity) 클래스 
├── dto                          // Request/Response DTO 
├── exception                    // 예외 처리 관련 클래스 
├── repository                   // 데이터베이스 접근 (JPA Repository) 
├── security                     // JWT 인증/인가 및 보안 관련 로직 
├── service                      // 비즈니스 로직 
└── CommunityBeApplication.java  // Spring Boot 메인 클래스
```
<details>
<summary>파일 설명</summary>
  
```java
katebu_community.community_be 
├── config 📁                      
│   ├── SecurityConfig        // 보안 구성, CORS 정책
│   └── WebConfig             // 웹 관련 설정
├── controller 📁
│   ├── AuthController        // 회원가입, 로그인 처리
│   ├── CommentController     // 댓글 CRUD
│   ├── PostController        // 게시글 CRUD, 게시글 목록 조회, 좋아요 추가/삭제
│   └── UserController        // 회원정보 조회/수정, 비밀번호 변경, 회원탈퇴 처리
├── domain 📁 
│   ├── Comment
│   ├── Likes
│   ├── Post
│   └── User
├── dto 📁                          
│   ├── ApiResponse            // 공통 응답 DTO 
│   ├── CommentListResponse    // 댓글 목록 조회 응답 data 
│   ├── CommentRequestDTO      // 댓글 작성 요청 body
│   ├── LoginRequestDto        // 로그인 요청 body
│   ├── LoginResponseDto       // 로그인 응답 data
│   ├── PageableInfoDto        // 페이징 정보 (PostListResponseDto에 포함)
│   ├── PostDetailDto          // 게시글 상세 조회 응답 data 
│   ├── PostListResponseDto    // 게시글 목록 조회 응답 data
│   ├── PostSummaryDto         // 게시글 요약 정보 (PostListResponseDto에 포함)
│   ├── UserDto                // 유저 정보 (Auth, User 기능에서 포함)
│   └── WriterDto              // 작성자 정보 (PostDetailDto에 포함)
├── exception 📁                     
│   ├── AlreadyLikedException
│   ├── AlreadyUnlikedException
│   ├── CommentNotFoundException
│   ├── DuplicateException
│   ├── EmailNotFoundException
│   ├── GlobalExceptionHandler  // 전역 예외 처리 핸들러 (컨트롤러에서 처리하지 않은 모든 예외 처리)
│   ├── InvalidInputException
│   ├── InvalidPasswordException
│   ├── PostNotFoundException
│   └── UnauthorizedException
├── repository 📁             
│   ├── CommentRepository
│   ├── LikesRepository
│   ├── PostRepository
│   └── UserRepository
├── security 📁             
│   ├── CustomAuthenticationEntryPoint // 인증 실패 처리 (401 반환)
│   ├── JwtAuthenticationFilter        // 인증 필터
│   └── JwtTokenProvider               // 토큰 생성, 검증, 아이디 추출
├── service 📁               
│   ├── AuthService
│   ├── CommentService
│   ├── FileUploadService       // 이미지 업로드/삭제 공통 서비스
│   ├── PostService
│   ├── UserCommonService       // 유저 조회 공통 서비스
│   └── UserService
└── CommunityBeApplication
```
</details>

## 4. 기능

[🔗 시연 영상 보러가기](https://www.youtube.com/watch?v=CDuhguLbHp4)

<details>
  <summary>서비스 화면 보기</summary>
  
|로그인|회원가입|
|---|---|
|![로그인](https://github.com/user-attachments/assets/76ec1a22-168e-496d-b510-35fa236e33cb)|![회원가입](https://github.com/user-attachments/assets/f66505a2-4643-421d-a4bb-2ed93cdea80c)|

|게시판(로그인)|게시판(비로그인)|
|---|---|
|![로그인](https://github.com/user-attachments/assets/d3c18cd4-f1d9-40a7-ad8d-9b2caaf7c777)|![회원가입](https://github.com/user-attachments/assets/97fb3853-dbf4-4a3c-9a04-c10d3b5fd3a0)|


|게시글|게시글 수정|게시글 작성|게시글 삭제|
|---|---|---|---|
|![게시글](https://github.com/user-attachments/assets/c0f78a82-c25b-4c03-96e5-82b2677fba6e)|![게시글 수정](https://github.com/user-attachments/assets/13ea96e0-857f-49d5-8cf0-681d06e5cda4)|![게시글 작성](https://github.com/user-attachments/assets/afbe5c59-2806-4d77-b519-e4263e255c45)|![게시글 삭제](https://github.com/user-attachments/assets/85f0a7f9-308e-4d4a-b8ee-6539a99b2229)|

|댓글|댓글 수정|댓글 삭제|
|---|---|---|
|![댓글](https://github.com/user-attachments/assets/d6293772-de59-4a81-8745-4157c18826af)|![댓글 수정](https://github.com/user-attachments/assets/74409b9a-ba43-483c-8419-9f41f536ccd9)|![댓글 삭제](https://github.com/user-attachments/assets/e0d174ba-0591-4662-8b2a-b23db30b45af)|

|회원정보 수정|비밀번호 변경|회원탈퇴|
|---|---|---|
|![회원정보 수정](https://github.com/user-attachments/assets/3f6ddcea-afe8-4bca-b732-98e84513b726)|![비밀번호 변경](https://github.com/user-attachments/assets/0dc29dc9-dadd-4bc9-bc6c-ff0d15af1a8f)|![회원탈퇴](https://github.com/user-attachments/assets/934623ed-ea73-4b9b-89f3-283306267b07)|
</details>

### 1) 사용자

| 기능                  | 설명                                |
|---------------------|-----------------------------------|
| 회원가입                | 프로필 이미지 함께 업로드                    |
| 로그인                 | 이메일과 비밀번호로 로그인. <br/>JWT 기반 인증으로 로그인 성공 시 토큰 반환 |
| 회원정보 조회             | JWT 토큰에 포함된 사용자 정보를 바탕으로 회원정보 조회. <br/>토큰이 만료되었거나 변조되었을 경우 예외 처리. <br/>헤더와 회원정보 수정 페이지에서 API 요청. 
| 회원정보 수정             | 닉네임과 프로필 이미지 변경                   |
| 비밀번호 변경             |                                   |
| 회원탈퇴                | hard-delete 방식. 연관된 게시글/댓글 함께 삭제  |

### 2) 게시글

| 기능        | 설명                                   |
|-----------|--------------------------------------|
| 게시글 작성    | 본문 이미지 함께 업로드                        |
| 게시글 수정    |                                      |
| 게시글 삭제    |                                      |
| 게시글 조회    | 로그인/비로그인 구분하여 좋아요 상태 응답. 비회원도 조회 가능. |
| 게시글 목록 조회 | Offset 페이징 방식. 비회원도 조회 가능.           |

### 3) 댓글

| 기능    | 설명          |
|-------|-------------|
| 댓글 작성 |             |
| 댓글 수정 |             |
| 댓글 삭제 |             |
| 댓글 조회 | 비회원도 조회 가능. |

### 4) 좋아요

| 기능         | 설명 |
|------------|----|
| 게시글 좋아요 추가 |    |
| 게시글 좋아요 삭제 |    |

### 5) 공통

| 기능         | 설명                                              |
|------------|-------------------------------------------------|
| DTO        |                                                 |
| 커스텀 예외     |                                                 |
| 이미지 업로드 기능 | 서버의 `/uploads` 폴더에 저장하고, 저장 경로를 DB에 기록. 파일 형식 검증. |

## 5. 기술적 고민 과정

- 유효하지 않은 토큰의 경우 401 Unauthorized 응답 구현 ([Issue #4](https://github.com/100-hours-a-week/2-jenny-son-community-be/issues/4))
    - `JwtAuthenticationFilter`에서 만료된 토큰 감지 후 401을 보내려 해도 `ExceptionTranslationFilter` 등으로 403이 반환되는 문제가 있어, `CustomAuthenticationEntryPoint`를 추가하여 401 응답을 보장하도록 했습니다. 
-
