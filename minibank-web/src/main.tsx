import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AccountsPage from "./pages/AccountsPage";
import "./index.css";

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <AccountsPage />          {/* 18.1절에서 라우터를 담은 <App />으로 바꾼다 */}
    </QueryClientProvider>
  </StrictMode>
);
