# Member API 사용 가이드

## 개요
이 문서는 Planit 서버의 Member 관련 API 사용법을 설명합니다. 모든 API는 RESTful 방식으로 구현되어 있으며, 인증이 필요한 API의 경우 JWT 토큰을 Authorization 헤더에 포함해야 합니다.

## 기본 정보
- **Base URL**: `https://your-domain.com/members`
- **Content-Type**: `application/json`
- **인증 방식**: Bearer Token (JWT)

## API 목록

### 1. 로그인/회원가입 (OAuth)

#### POST `/members/sign-in`
소셜 로그인을 통해 사용자 인증을 처리합니다. 신규 사용자의 경우 자동으로 회원가입이 진행됩니다.

**요청 헤더:**
```
Content-Type: application/json
```

**요청 본문:**
```json
{
  "oauthProvider": "KAKAO",
  "oauthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**oauthProvider 옵션:**
- `KAKAO`: 카카오 로그인
- `NAVER`: 네이버 로그인  
- `GOOGLE`: 구글 로그인

**응답 예시:**
```json
{
  "success": true,
  "code": "MEMBER_SIGN_IN_SUCCESS",
  "message": "로그인에 성공했습니다.",
  "data": {
    "id": 123,
    "email": "user@example.com",
    "name": "홍길동",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "isNewMember": false,
    "isSignUpCompleted": true
  }
}
```

**응답 필드 설명:**
- `isNewMember`: 신규 회원 여부 (true: 신규, false: 기존)
- `isSignUpCompleted`: 약관 동의 완료 여부
- `accessToken`: API 호출 시 사용할 액세스 토큰
- `refreshToken`: 액세스 토큰 갱신 시 사용할 리프레시 토큰

**사용 시나리오:**
1. 앱 시작 시 또는 로그인 버튼 클릭 시 호출
2. `isNewMember`가 true인 경우 약관 동의 화면으로 이동
3. `isSignUpCompleted`가 false인 경우 약관 동의 API 호출 필요

---

### 2. 로그아웃

#### POST `/members/sign-out`
사용자 로그아웃을 처리하고 현재 액세스 토큰을 블랙리스트에 추가합니다.

**요청 헤더:**
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**요청 본문:** 없음

**응답 예시:**
```json
{
  "success": true,
  "code": "MEMBER_SIGN_OUT_SUCCESS",
  "message": "로그아웃에 성공했습니다.",
  "data": null
}
```

**사용 시나리오:**
- 로그아웃 버튼 클릭 시 호출
- 앱 종료 시 자동 호출 (선택사항)

---

### 3. 약관 동의 완료

#### POST `/members/terms`
사용자가 약관에 동의했음을 서버에 저장하고 회원가입을 완료합니다.

**요청 헤더:**
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**요청 본문:**
```json
{
  "termOfUse": "2024-01-15T10:30:00",
  "termOfPrivacy": "2024-01-15T10:30:00",
  "termOfInfo": "2024-01-15T10:30:00",
  "overFourteen": "2024-01-15T10:30:00"
}
```

**필드 설명:**
- `termOfUse`: 이용약관 동의 시간
- `termOfPrivacy`: 개인정보처리방침 동의 시간
- `termOfInfo`: 정보제공 동의 시간
- `overFourteen`: 만 14세 이상 동의 시간

**응답 예시:**
```json
{
  "success": true,
  "code": "MEMBER_TERM_AGREEMENT_COMPLETED",
  "message": "약관 동의가 완료되었습니다.",
  "data": null
}
```

**사용 시나리오:**
- 신규 회원이 약관 동의 화면에서 모든 약관에 동의 후 호출
- 한 번만 호출하면 되며, 이후에는 `isSignUpCompleted`가 true로 설정됨

---

### 4. 연속일 조회

#### GET `/members/consecutive-days`
사용자의 연속 출석일 정보를 조회합니다.

**요청 헤더:**
```
Authorization: Bearer {accessToken}
```

**요청 본문:** 없음

**응답 예시:**
```json
{
  "success": true,
  "code": "MEMBER_CONSECUTIVE_DAYS_FOUND",
  "message": "연속일 조회에 성공했습니다.",
  "data": {
    "currentConsecutiveDays": 7,
    "maxConsecutiveDays": 15,
    "perfectConsecutiveDays": 5
  }
}
```

**응답 필드 설명:**
- `currentConsecutiveDays`: 현재 연속 출석일 수
- `maxConsecutiveDays`: 최대 연속 출석일 수 (역대 최고 기록)
- `perfectConsecutiveDays`: 완벽 연속 출석일 수 (구제 신청 없이)

**사용 시나리오:**
- 메인 화면에서 사용자 통계 표시
- 프로필 화면에서 사용자 정보 표시

---

## 에러 처리

### 공통 에러 응답 형식
```json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "에러 메시지",
  "data": null
}
```

### 주요 에러 코드
- `INVALID_OAUTH_TOKEN`: 유효하지 않은 OAuth 토큰
- `INVALID_ACCESS_TOKEN`: 유효하지 않은 액세스 토큰
- `MEMBER_NOT_FOUND`: 회원을 찾을 수 없음
- `TERMS_NOT_AGREED`: 약관 동의가 필요함

---

## 토큰 관리

### 액세스 토큰 사용법
```dart
// Flutter 예시
final response = await http.get(
  Uri.parse('https://your-domain.com/members/consecutive-days'),
  headers: {
    'Authorization': 'Bearer $accessToken',
    'Content-Type': 'application/json',
  },
);
```

### 토큰 저장 및 관리
1. 로그인 성공 시 받은 `accessToken`과 `refreshToken`을 안전하게 저장
2. 모든 API 호출 시 `Authorization` 헤더에 액세스 토큰 포함
3. 액세스 토큰 만료 시 리프레시 토큰을 사용하여 갱신 (별도 API 필요)

---

## 구현 예시 (Flutter)

### 로그인 처리
```dart
Future<void> signIn(String oauthProvider, String oauthToken) async {
  try {
    final response = await http.post(
      Uri.parse('https://your-domain.com/members/sign-in'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'oauthProvider': oauthProvider,
        'oauthToken': oauthToken,
      }),
    );

    final data = jsonDecode(response.body);
    
    if (data['success']) {
      // 토큰 저장
      await _saveTokens(data['data']['accessToken'], data['data']['refreshToken']);
      
      // 신규 회원인 경우 약관 동의 화면으로 이동
      if (data['data']['isNewMember']) {
        Navigator.pushNamed(context, '/terms');
      }
    }
  } catch (e) {
    print('로그인 실패: $e');
  }
}
```

### 인증이 필요한 API 호출
```dart
Future<Map<String, dynamic>> getConsecutiveDays() async {
  final accessToken = await _getAccessToken();
  
  final response = await http.get(
    Uri.parse('https://your-domain.com/members/consecutive-days'),
    headers: {
      'Authorization': 'Bearer $accessToken',
      'Content-Type': 'application/json',
    },
  );

  return jsonDecode(response.body);
}
```

---

## 주의사항

1. **토큰 보안**: 액세스 토큰과 리프레시 토큰을 안전하게 저장하세요 (Flutter의 경우 `flutter_secure_storage` 사용 권장)
2. **에러 처리**: 모든 API 호출에 적절한 에러 처리를 구현하세요
3. **토큰 만료**: 액세스 토큰이 만료되면 자동으로 리프레시 토큰을 사용하여 갱신하는 로직을 구현하세요
4. **네트워크 상태**: 네트워크 연결 상태를 확인하고 적절한 사용자 피드백을 제공하세요

---

## 문의사항

API 사용 중 문제가 발생하거나 추가 문의사항이 있으시면 백엔드 개발팀에 연락해주세요. 