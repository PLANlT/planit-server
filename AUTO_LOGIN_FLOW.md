# 자동 로그인 API 호출 흐름도

## 개요
플래닛 서버의 자동 로그인은 Refresh Token을 사용하여 새로운 Access Token을 발급받는 방식으로 동작합니다.

## API 엔드포인트
```
POST /planit/auth/refresh
```

## 요청 헤더
```
Authorization: Bearer {refresh_token}
```

## 전체 흐름도

```mermaid
sequenceDiagram
    participant Client as 📱 플러터 앱
    participant Controller as 🎮 AuthController
    participant Service as ⚙️ AuthServiceImpl
    participant JWT as 🔐 JwtProvider
    participant Redis as 🗄️ Redis
    participant Member as 👤 MemberService
    participant DB as 💾 Database

    Note over Client, DB: 🚀 앱 시작 시 자동 로그인 시도
    Client->>Controller: POST /planit/auth/refresh<br/>Authorization: {refresh_token}
    
    Note over Controller: 🔍 1. 요청 헤더 검증
    Controller->>Controller: refreshTokenHeader null 체크
    alt ❌ 헤더가 null인 경우
        Controller-->>Client: 401 INVALID_REFRESH_TOKEN
    end
    
    Note over Controller: ✂️ 2. Bearer 제거 처리
    Controller->>Controller: Bearer 접두사 제거<br/>refreshToken 추출
    
    Note over Service: 🔄 3. 토큰 갱신 프로세스 시작
    Controller->>Service: refreshAccessToken(refreshToken)
    
    Note over Service: ⏰ 4. Refresh Token 만료 여부 확인
    Service->>JWT: isTokenExpired(refreshToken)
    alt ❌ 토큰이 만료된 경우
        JWT-->>Service: true
        Service-->>Controller: 401 REFRESH_TOKEN_EXPIRED
        Controller-->>Client: 401 에러 응답
    end
    
    Note over Service: 🔒 5. Refresh Token 위조 여부 확인
    Service->>JWT: isRefreshTokenTampered(refreshToken)
    alt ❌ 토큰이 위조된 경우
        JWT-->>Service: true
        Service-->>Controller: 401 INVALID_REFRESH_TOKEN
        Controller-->>Client: 401 에러 응답
    end
    
    Note over Service: 🆔 6. 토큰에서 Member ID 추출
    Service->>JWT: getId(refreshToken)
    JWT-->>Service: memberId
    
    Note over Service: 🔍 7. Redis에 저장된 토큰과 일치 여부 확인
    Service->>Redis: getRefreshTokenByMemberId(memberId)
    Redis-->>Service: savedToken
    
    alt ❌ Redis 토큰과 불일치하는 경우
        Service-->>Controller: 401 INVALID_REFRESH_TOKEN
        Controller-->>Client: 401 에러 응답
    end
    
    Note over Service: 👤 8. 회원 정보 조회
    Service->>Member: getSignedMemberById(memberId)
    Member->>DB: SELECT * FROM member WHERE id = ?
    DB-->>Member: member 데이터
    Member-->>Service: SignedMember 객체
    
    Note over Service: 🆕 9. 새로운 Access Token 생성
    Service->>JWT: createAccessToken(id, email, name, role)
    JWT-->>Service: newAccessToken
    
    Note over Service: 📦 10. 응답 데이터 구성
    Service->>Service: TokenRefreshDTO.Response.builder()<br/>.accessToken(newAccessToken)<br/>.refreshToken(refreshToken)<br/>.id(signedMember.getId())<br/>.email(signedMember.getEmail())<br/>.name(signedMember.getName())<br/>.role(signedMember.getRole().toString())<br/>.build()
    
    Note over Controller: ✅ 11. 성공 응답 반환
    Service-->>Controller: TokenRefreshDTO.Response
    Controller-->>Client: 200 SUCCESS<br/>{<br/>  "success": true,<br/>  "message": "토큰 갱신 성공",<br/>  "data": {<br/>    "accessToken": "new_access_token",<br/>    "refreshToken": "original_refresh_token",<br/>    "id": 1,<br/>    "email": "user@example.com",<br/>    "name": "사용자명",<br/>    "role": "USER"<br/>  }<br/>}
```

## 에러 케이스별 흐름도

```mermaid
flowchart TD
    A[📱 플러터 앱 시작] --> B{🔑 Refresh Token 존재?}
    B -->|❌ 없음| C[🚪 로그인 화면으로 이동]
    B -->|✅ 있음| D[🔄 /planit/auth/refresh 호출]
    
    D --> E{🔍 Authorization 헤더 검증}
    E -->|❌ null| F[401 INVALID_REFRESH_TOKEN]
    E -->|✅ 유효| G[✂️ Bearer 제거]
    
    G --> H{⏰ 토큰 만료 확인}
    H -->|❌ 만료됨| I[401 REFRESH_TOKEN_EXPIRED<br/>🚪 로그인 화면으로 이동]
    H -->|✅ 유효| J{🔒 토큰 위조 확인}
    
    J -->|❌ 위조됨| K[401 INVALID_REFRESH_TOKEN<br/>🚪 로그인 화면으로 이동]
    J -->|✅ 정상| L[🆔 Member ID 추출]
    
    L --> M{🔍 Redis 토큰 일치 확인}
    M -->|❌ 불일치| N[401 INVALID_REFRESH_TOKEN<br/>🚪 로그인 화면으로 이동]
    M -->|✅ 일치| O[👤 회원 정보 조회]
    
    O --> P{💾 회원 존재 확인}
    P -->|❌ 없음| Q[401 MEMBER_NOT_FOUND<br/>🚪 로그인 화면으로 이동]
    P -->|✅ 존재| R[🆕 새 Access Token 생성]
    
    R --> S[📦 응답 데이터 구성]
    S --> T[✅ 200 SUCCESS<br/>🔄 자동 로그인 완료]
    
    style A fill:#e1f5fe
    style T fill:#c8e6c9
    style C fill:#ffcdd2
    style I fill:#ffcdd2
    style K fill:#ffcdd2
    style N fill:#ffcdd2
    style Q fill:#ffcdd2
    style F fill:#ffcdd2
```

## 토큰 생명주기 다이어그램

```mermaid
gantt
    title 자동 로그인 토큰 생명주기
    dateFormat  YYYY-MM-DD
    section Access Token
    Access Token 생성    :a1, 2024-01-01, 2h
    Access Token 사용    :a2, after a1, 2h
    Access Token 만료    :a3, after a2, 0h
    
    section Refresh Token
    Refresh Token 생성   :r1, 2024-01-01, 30d
    Refresh Token 사용   :r2, after r1, 30d
    Refresh Token 만료   :r3, after r2, 0h
    
    section 자동 로그인
    앱 시작 시도        :app1, 2024-01-01, 1h
    토큰 갱신 성공      :refresh1, after app1, 1h
    새 Access Token 발급 :new1, after refresh1, 2h
```

## 상세 프로세스 설명

### 1. 앱 시작 시 자동 로그인
- 플러터 앱이 시작될 때 저장된 Refresh Token을 사용하여 자동 로그인을 시도합니다.
- Refresh Token은 30일간 유효하며, 이 기간 동안 자동 로그인이 가능합니다.

### 2. 요청 헤더 검증
- `Authorization` 헤더가 존재하는지 확인합니다.
- 헤더가 null인 경우 `INVALID_REFRESH_TOKEN` 에러를 반환합니다.

### 3. Bearer 제거 처리
- `Bearer ` 접두사가 있으면 제거하고, 없으면 그대로 사용합니다.
- 두 가지 방식 모두 지원하여 클라이언트 구현의 유연성을 제공합니다.

### 4. Refresh Token 검증
- **만료 여부 확인**: JWT의 만료 시간을 확인하여 토큰이 만료되었는지 검증합니다.
- **위조 여부 확인**: 토큰의 서명을 검증하여 위조되지 않았는지 확인합니다.

### 5. Redis 토큰 일치 확인
- 토큰에서 추출한 Member ID를 사용하여 Redis에 저장된 Refresh Token과 일치하는지 확인합니다.
- 이는 토큰 재사용 공격을 방지하는 보안 메커니즘입니다.

### 6. 회원 정보 조회
- Member ID를 사용하여 데이터베이스에서 회원 정보를 조회합니다.
- 회원이 존재하지 않는 경우 에러를 반환합니다.

### 7. 새로운 Access Token 생성
- 회원 정보를 바탕으로 새로운 Access Token을 생성합니다.
- Access Token의 유효기간은 2시간입니다.

### 8. 응답 반환
- 새로운 Access Token과 기존 Refresh Token, 회원 정보를 포함한 응답을 반환합니다.
- Refresh Token은 만료될 때까지 재사용됩니다.

## 에러 케이스

### 1. Refresh Token 만료
```json
{
  "success": false,
  "message": "Refresh Token이 만료되었습니다",
  "errorCode": "REFRESH_TOKEN_EXPIRED"
}
```

### 2. Refresh Token 위조
```json
{
  "success": false,
  "message": "유효하지 않은 Refresh Token입니다",
  "errorCode": "INVALID_REFRESH_TOKEN"
}
```

### 3. Redis 토큰 불일치
```json
{
  "success": false,
  "message": "유효하지 않은 Refresh Token입니다",
  "errorCode": "INVALID_REFRESH_TOKEN"
}
```

## 보안 고려사항

1. **토큰 만료 시간**: Refresh Token은 30일, Access Token은 2시간으로 설정되어 있습니다.
2. **Redis 저장**: Refresh Token은 Redis에 저장되어 토큰 재사용 공격을 방지합니다.
3. **서명 검증**: JWT 서명을 검증하여 토큰 위조를 방지합니다.
4. **블랙리스트**: 로그아웃 시 Access Token을 블랙리스트에 추가합니다.

## 성능 최적화

1. **Redis 캐싱**: Refresh Token을 Redis에 저장하여 빠른 조회가 가능합니다.
2. **로그 레벨**: 상세한 로그를 통해 디버깅과 모니터링을 지원합니다.
3. **에러 처리**: 각 단계별로 적절한 에러 처리를 통해 안정성을 보장합니다. 