import { NavLink, Outlet } from "react-router";
import { useAuth } from "../auth/AuthContext";

export default function Layout() {
  const { name, logout } = useAuth();
  return (
    <div className="app">
      <header className="topbar">
        <strong>미니뱅크</strong>
        <nav>
          <NavLink to="/">내 계좌</NavLink>
          <NavLink to="/transfer">이체</NavLink>
        </nav>
        <span>{name}님</span>
        <button onClick={logout}>로그아웃</button>
      </header>
      <main><Outlet /></main>          {/* 자식 경로의 화면이 여기 그려진다 */}
    </div>
  );
}
