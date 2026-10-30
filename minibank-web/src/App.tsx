import { BrowserRouter, Routes, Route } from "react-router";
import Layout from "./components/Layout";
import LoginPage from "./pages/LoginPage";
import AccountsPage from "./pages/AccountsPage";
import TransferPage from "./pages/TransferPage";
import HistoryPage from "./pages/HistoryPage";
import RequireAuth from "./auth/RequireAuth";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<RequireAuth><Layout /></RequireAuth>}>   {/* 로그인 필요 */}
          <Route path="/" element={<AccountsPage />} />
          <Route path="/transfer" element={<TransferPage />} />
          <Route path="/accounts/:accountNumber" element={<HistoryPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
