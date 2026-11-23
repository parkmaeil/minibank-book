import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import App from "./App";
import { ApiError } from "./api/client";
import { AuthProvider } from "./auth/AuthContext";
import "./index.css";

// 18.6절 4xx(로그인 필요·남의 계좌 등)는 다시 시도해도 결과가 같으므로 바로 실패시키고,
// 네트워크 오류·5xx만 2번까지 다시 시도한다 (기본값은 무엇이든 3번 재시도)
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: (failureCount, error) =>
        !(error instanceof ApiError && error.status < 500) && failureCount < 2,
    },
  },
});

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>               {/* 18.8절 로그인 상태를 앱 전체에 공유 */}
        <App />
      </AuthProvider>
    </QueryClientProvider>
  </StrictMode>
);
