# minibank-web — 미니뱅크 고객용 웹뱅킹 (React + TypeScript)

책 5부(16~19장)에서 만드는 프론트엔드입니다.

```bash
npm install
npm run dev        # http://localhost:5173  (/api 요청은 vite.config.ts의 프록시가 8080으로 전달)
npm run build      # tsc 타입 검사 + 프로덕션 빌드(dist/)
```

로그인 계정은 직원이 발급합니다(19.5절).

```bash
curl -u admin:admin1234! -X POST http://localhost:8080/api/customers/1/login \
     -H "Content-Type: application/json" -d '{"username":"hong","password":"hong1234!"}'
```
