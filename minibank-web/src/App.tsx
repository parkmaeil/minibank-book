import { BrowserRouter, Routes, Route } from "react-router";
import Layout from "./components/Layout";
import LoginPage from "./pages/LoginPage";
import AccountsPage from "./pages/AccountsPage";
import RequireAuth from "./auth/RequireAuth";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<RequireAuth><Layout /></RequireAuth>}>   {/* 로그인 필요 */}
          <Route path="/" element={<AccountsPage />} />
          {/* 19장에서 이체(/transfer)와 거래내역(/accounts/:accountNumber) 경로를 더한다 */}
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
