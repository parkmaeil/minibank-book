import { useState } from "react";
import AccountCard from "../components/AccountCard";
import type { Account } from "../types";

// 17장에서 실제 API로 바꾸기 전까지 쓰는 임시 데이터
const SAMPLE: Account[] = [
  { id: 1, accountNumber: "110-9221-2776", ownerName: "홍길동", balance: 30000,
    dailyLimit: 3000000, openedAt: "2026-10-01T10:00:00" },
  { id: 2, accountNumber: "110-2454-5518", ownerName: "홍길동", balance: 20000,
    dailyLimit: 3000000, openedAt: "2026-10-01T10:05:00" },
];

export default function AccountsPage() {
  const [hidden, setHidden] = useState(false);     // [현재 값, 바꾸는 함수]

  const total = SAMPLE.reduce((sum, a) => sum + a.balance, 0);

  return (
    <section>
      <h2>내 계좌</h2>
      <p>총 잔액: {hidden ? "••••••" : total.toLocaleString("ko-KR") + "원"}</p>
      <button onClick={() => setHidden(!hidden)}>{hidden ? "잔액 보기" : "잔액 숨기기"}</button>
      <div className="card-list">
        {SAMPLE.map((a) => (
          <AccountCard key={a.id} account={a}
                       onShowHistory={(no) => alert(no + " 거래내역")} />
        ))}
      </div>
    </section>
  );
}
