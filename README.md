# 아무 말 대잔치 커뮤니티 백엔드 프로젝트

## 1. 프로젝트 소개

Spring Boot 기반의 커뮤니티 백엔드 애플리케이션입니다.

주요 기능으로는 유저 인증(JWT 기반), 게시글/댓글 작성, 좋아요, 이미지 업로드가 있습니다.

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
├── config                       # 보안 설정(SecurityConfig) 및 글로벌 설정 
├── controller                   # REST API 엔드포인트 담당 
├── domain                       # 엔티티(Entity) 클래스 
├── dto                          # Request/Response DTO 
├── exception                    # 예외 처리 관련 클래스 
├── repository                   # 데이터베이스 접근 (JPA Repository) 
├── security                     # JWT 인증/인가 및 보안 관련 로직 
├── service                      # 비즈니스 로직 
└── CommunityBeApplication.java  # Spring Boot 메인 클래스
```

## 4. 기능

[🔗 시연 영상 보러가기](https://www.youtube.com/watch?v=CDuhguLbHp4)

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
