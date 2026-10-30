import { NavLink, Outlet } from "react-router";

// 18.1절 — 로그인한 고객 이름과 로그아웃 버튼은 19.5절에서 추가한다
export default function Layout() {
  return (
    <div className="app">
      <header className="topbar">
        <strong>미니뱅크</strong>
        <nav>
          <NavLink to="/">내 계좌</NavLink>
          <NavLink to="/transfer">이체</NavLink>
        </nav>
      </header>
      <main><Outlet /></main>          {/* 자식 경로의 화면이 여기 그려진다 */}
    </div>
  );
}
