import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import AccountCard from "../components/AccountCard";
import { api } from "../api/client";
import type { Account } from "../types";

// 17.6절 TanStack Query 버전
export default function AccountsPage() {
  const [hidden, setHidden] = useState(false);
  const navigate = useNavigate();
  const { data: accounts, isPending, isError, error } = useQuery({
    queryKey: ["myAccounts"],                          // 이 데이터의 이름(캐시 키)
    queryFn: () => api<Account[]>("/api/me/accounts"),
  });

  if (isPending) return <p>불러오는 중...</p>;
  if (isError) return <p className="error">{error.message}</p>;

  const total = accounts.reduce((sum, a) => sum + a.balance, 0);
  return (
    <section>
      <h2>내 계좌</h2>
      <p>총 잔액: {hidden ? "••••••" : total.toLocaleString("ko-KR") + "원"}</p>
      <button onClick={() => setHidden(!hidden)}>{hidden ? "잔액 보기" : "잔액 숨기기"}</button>
      <div className="card-list">
        {accounts.map((a) => (
          <AccountCard key={a.id} account={a}
                       onShowHistory={(no) => navigate(`/accounts/${no}`)}   /* 18.3절 */ />
        ))}
      </div>
    </section>
  );
}
