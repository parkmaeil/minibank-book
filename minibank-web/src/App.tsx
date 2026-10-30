import { BrowserRouter, Routes, Route } from "react-router";
import Layout from "./components/Layout";
import AccountsPage from "./pages/AccountsPage";
import TransferPage from "./pages/TransferPage";
import HistoryPage from "./pages/HistoryPage";

// 18.1절 — 로그인 화면(/login)과 RequireAuth는 19.5절에서 추가한다
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>                {/* 공통 레이아웃 */}
          <Route path="/" element={<AccountsPage />} />
          <Route path="/transfer" element={<TransferPage />} />
          <Route path="/accounts/:accountNumber" element={<HistoryPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
