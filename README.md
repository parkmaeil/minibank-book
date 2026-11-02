# 미니뱅크로 배우는 Spring Boot 풀스택 — 예제 코드

『미니뱅크로 배우는 Spring Boot 풀스택』(박매일 지음, IT'S CODING)의 공식 예제 저장소입니다.

| 폴더 | 내용 | 등장 |
|---|---|---|
| `mini-bank/` | Spring Boot 4 백엔드 — REST API, JPA, Vaadin 직원용 관리 화면, Spring Security | 2~20장 |
| `minibank-web/` | React + TypeScript 고객용 웹뱅킹 | 16~19장 |
| `docker-compose.yml` | MySQL + 백엔드 + Nginx(React) 풀스택 실행 | 19장 |

## 태그로 장 이동하기

장마다 **시작 상태(`chNN-start`)** 와 **완성 상태(`chNN-end`)** 가 태그로 나뉘어 있습니다.
`chNN-start`는 바로 앞 장의 완성 상태와 같습니다.

```bash
git clone https://github.com/parkmaeil/minibank-book.git
cd minibank-book

git tag                                   # 태그 목록 보기
git switch -c my-ch06 ch06-start          # 6장 시작 상태에서 내 브랜치를 만들어 따라 하기
git diff ch06-start ch06-end --stat       # 6장에서 바뀐 파일 목록
git diff ch06-start ch06-end -- mini-bank/src/main/java   # 6장에서 바뀐 코드 전체
git diff my-ch06 ch06-end                 # 내 코드와 완성본 비교 — 막혔을 때
```

IntelliJ IDEA에서는 **Git → Branches → Checkout Tag or Revision…** 에 태그 이름을 입력하면 됩니다.

### 내가 직접 만든 프로젝트와 비교하기

2장에서 Initializr로 직접 만든 `mini-bank` 프로젝트로 계속 따라 한다면, 이 저장소를 `book`이라는 이름으로 연결해 비교합니다.
저장소에는 `mini-bank/` 폴더가 한 단계 더 있으므로, 태그 이름 뒤에 `:mini-bank`를 붙입니다.

```
study/
 ├─ mini-bank/        ← 내가 따라 치는 프로젝트
 └─ minibank-book/    ← 이 저장소 (git clone)
```

```bash
cd study/mini-bank
git remote add book ../minibank-book       # 한 번만
git fetch book --tags                      # 저장소를 새로 받았을 때마다

git commit -am "6장 따라 하기"              # 장을 마칠 때마다 커밋
git diff -w --stat ch06-end:mini-bank HEAD    # 완성본과 다른 파일 목록 (-w: 공백 차이 무시)
git diff -w ch06-end:mini-bank HEAD -- src/main/java/com/itscoding/minibank/service/AccountService.java

# 파일 하나만 완성본으로 가져오기 (막혔을 때)
git show ch06-end:mini-bank/src/main/java/com/itscoding/minibank/service/AccountService.java > src/main/java/com/itscoding/minibank/service/AccountService.java
```

> Windows PowerShell 5에서 `>`로 저장하면 파일이 UTF-16으로 바뀌어 한글 주석이 든 Java 파일이 깨집니다. 이 명령은 Git Bash에서 실행하거나, GitHub 웹에서 태그를 고른 뒤 파일 내용을 복사해 붙여 넣으세요.

`gradle/wrapper/*`, `gradlew`, `gradlew.bat`은 Initializr로 프로젝트를 만든 날짜에 따라 Gradle 버전이 달라 차이가 날 수 있으며, 무시해도 됩니다.

**diff가 0일 필요는 없습니다.** 비교는 채점이 아니라 막혔을 때 원인을 찾는 도구입니다. 내 코드가 맞는지는 이 순서로 확인하세요.

1. 실행 결과가 책과 같은지, 6장부터는 `gradlew test`가 통과하는지 확인합니다. 통과하면 끝입니다.
2. 막혔다면 `git diff --stat ch06-start ch06-end`로 이 장이 바꾼 파일만 골라 `git diff -w`로 비교합니다.
3. 들여쓰기·빈 줄·주석·메서드 순서·변수 이름 차이는 무시하고, 빠진 애노테이션·의존성·설정 값을 찾습니다.
4. 그래도 안 되면 파일을 가져오거나 다음 장의 `chNN-start`에서 이어 갑니다.

16장부터 만드는 `minibank-web`도 같은 방법으로 `ch16-end:minibank-web`과 비교합니다.
한 장에서 너무 막혔다면 이 저장소에서 `git switch -c my-ch07 ch07-start`로 다음 장의 시작 상태를 받아 이어서 따라 해도 됩니다.

| 장 | 완성 태그 | 이 장에서 추가·변경되는 것 |
|---|---|---|
| 2장 프로젝트 시작 | `ch02-end` | Initializr 프로젝트, `Customer`·`Account` POJO, `.gitignore` |
| 3장 Spring Boot의 동작 원리 | `ch03-end` | dev/prod 프로파일, `Clock` Bean, `@ConfigurationProperties`, 파일 로그 |
| 4장 첫 번째 REST API | `ch04-end` | DTO·Bean Validation, 인메모리 Repository, Service·Controller, 전역 예외 처리, Swagger, `@AccountNumber` |
| 5장 JPA | `ch05-end` | MySQL(docker compose), 엔티티·`@Version`, `JpaRepository`, 비관적 락 조회, Flyway `V1` |
| 6장 고객 관리와 연관관계 | `ch06-end` | 고객 CRUD API, 고객-계좌 연결, 이름 검색, 샘플 데이터, 동기화 테스트 |
| 7장 계좌 이체와 트랜잭션 | `ch07-end` | `transfer()`, 예외 처리 완성, `AuditService`(REQUIRES_NEW), `TransferFacade`(재시도), 동시 출금 테스트 |
| 8장 N+1 문제 | `ch08-end` | JOIN FETCH, `@EntityGraph`, DTO 프로젝션, 페이징 API |
| 9장 Vaadin 관리 화면 | `ch09-end` | `MainLayout`·고객/계좌 화면·개설 Dialog·Binder 검증·금액 서식 |
| 10장 테스트 자동화 | `ch10-end` | Mockito·MockMvc·`@DataJpaTest`·Testcontainers 테스트, JaCoCo |
| 11장 빌드와 Docker 배포 | `ch11-end` | Vaadin 프로덕션 빌드, 멀티 스테이지 Dockerfile, Compose+healthcheck, `.env`, GitHub Actions |
| 12장 이체 화면과 거래내역 | `ch12-end` | 이체 Dialog, `TransactionHistory`, 거래내역 조회·기간 검색·CSV |
| 13장 1일 이체 한도(도전 과제) | `ch13-end` | 한도 검증·변경 API, 계좌 상세 Dialog — 예시 답안 |
| 14장 Spring Security | `ch14-end` | Basic 인증, 역할별 URL 규칙, BCrypt, 보안 테스트 |
| 15장 이벤트와 스케줄링 | `ch15-end` | 이체 완료 이벤트(AFTER_COMMIT·`@Async`), 이자 지급 배치 |
| 16장 React 시작 | `ch16-end` | Vite react-ts 프로젝트, 타입, 계좌 카드 |
| 17장 REST API 연결 | `ch17-end` | API 클라이언트, Vite 프록시, 백엔드 CORS, TanStack Query |
| 18장 웹뱅킹 화면 | `ch18-end` | React Router, 이체 폼, 거래내역 페이지 |
| 19장 로그인과 풀스택 배포 | `ch19-end` | JWT 로그인·내 계좌 API·소유권 검사, React 로그인, Nginx·Compose |
| 20장 책을 마치며 | `ch20-end` | Actuator |

1장은 코드가 없으므로 태그가 없습니다. 13장은 도전 과제이므로, 직접 풀어 본 뒤에 `ch13-end`와 비교하세요.

## 실행 환경

- JDK 21, Docker Desktop, (16장부터) Node.js 22 이상
- 빌드 도구는 Gradle Wrapper(`./gradlew`, Windows는 `gradlew.bat`)를 쓰므로 Gradle을 따로 설치할 필요가 없습니다.

### 백엔드 실행 (5장 이후)

```bash
cd mini-bank
docker compose up -d mysql        # MySQL 8.4 실행 (5장)
./gradlew bootRun                 # http://localhost:8080  (Vaadin 화면), /swagger-ui.html
```

- **11장부터** DB 비밀번호를 환경 변수로 받습니다. IntelliJ Run Configuration의 *Environment variables*에 `DB_PASSWORD=minibank1234!`를 넣거나,
  터미널에서 `DB_PASSWORD=minibank1234! ./gradlew bootRun`으로 실행하세요. `docker compose`는 `.env.example`을 `.env`로 복사해 씁니다.
- **14장부터** REST API는 직원 계정으로 호출합니다: `curl -u teller:teller1234! http://localhost:8080/api/accounts` (관리자 `admin`/`admin1234!`)

### 테스트

```bash
cd mini-bank
./gradlew build                   # 컴파일 + 테스트 + JaCoCo 보고서
```

- 5~9장 태그의 `@SpringBootTest`는 로컬 MySQL(`docker compose up -d mysql`)이 떠 있어야 통과합니다.
- 10장부터는 Testcontainers가 테스트용 MySQL을 직접 띄우므로 **Docker만 실행 중이면** 됩니다. GitHub Actions에서도 그대로 돌아갑니다.

### 프론트엔드 실행 (16장 이후)

```bash
cd minibank-web
npm install
npm run dev                       # http://localhost:5173
```

19장에서 고객 로그인 계정을 발급한 뒤 로그인합니다.

```bash
curl -u admin:admin1234! -X POST http://localhost:8080/api/customers/1/login \
     -H "Content-Type: application/json" -d '{"username":"hong","password":"hong1234!"}'
```

### 풀스택 한 번에 실행 (19장)

```bash
cp .env.example .env
docker compose up --build         # http://localhost  (hong / hong1234!)
```

## 모든 태그 빌드 검증

출간 전·버전 업그레이드 후에 모든 장의 코드가 빌드되는지 한 번에 확인하는 스크립트입니다.

```bash
./scripts/verify-tags.sh          # macOS·Linux·Git Bash
```
```powershell
.\scripts\verify-tags.ps1         # Windows PowerShell
```

각 태그를 임시 폴더에 풀어 백엔드는 `compileJava compileTestJava`, 프론트엔드는 `npm ci && npm run build`를 실행하고,
마지막 태그(`ch20-end`)에서는 테스트까지 포함한 `./gradlew build`를 실행합니다(Docker 필요).

## 책과 다른 점

책의 코드 조각은 설명을 위해 일부를 생략했습니다. 이 저장소는 생략한 부분까지 채운 **실행 가능한 전체 코드**이며,
조각만으로는 컴파일되지 않거나 최신 도구에서 바뀐 부분을 다음과 같이 보완했습니다.

- `ui/AppShell.java` — Vaadin 25부터 기본 테마가 Aura로 바뀌어, 책 화면(Lumo 기준)과 같게 보이도록 `@StyleSheet(Lumo.STYLESHEET)`을 지정했습니다.
- `TestcontainersConfiguration` — `@SpringBootTest`·`@DataJpaTest`가 로컬 MySQL 없이 돌도록 10장부터 공통 설정으로 묶었습니다.
- `support/TestDtos` — 책에서 "도우미 메서드(생략)"으로 표시한 테스트용 DTO 생성 메서드입니다.
- Flyway 마이그레이션 번호 — 장 순서대로 `V1` 초기 스키마 → `V2` 감사 기록 → `V3` 거래내역 → `V4` 1일 한도 → `V5` 이자 기록 → `V6` 고객 로그인.
- `minibank-web/src/api/client.ts` — 최신 Vite 템플릿의 `erasableSyntaxOnly` 설정 때문에 `ApiError`의 `status`를 필드로 선언했습니다.
- 18장 태그의 `App.tsx`·`Layout.tsx`에는 아직 로그인 관련 코드가 없고, 19장에서 추가됩니다.

오류나 개선 제안은 Issues에 남겨 주세요.
