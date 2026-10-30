import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { api } from "../api/client";
import type { Account } from "../types";

export default function TransferPage() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { data: accounts = [] } = useQuery({
    queryKey: ["myAccounts"],
    queryFn: () => api<Account[]>("/api/me/accounts"),
  });

  // 입력값은 모두 state가 관리한다(제어 컴포넌트)
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [amount, setAmount] = useState("");

  const mutation = useMutation({
    mutationFn: () => api<void>("/api/me/transfers", {
      method: "POST",
      body: JSON.stringify({ fromAccountNumber: from, toAccountNumber: to, amount: Number(amount) }),
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["myAccounts"] });   // 잔액 다시 불러오기
      navigate("/");
    },
  });

  const error = validate(from, to, amount, accounts);

  return (
    <form onSubmit={(e) => { e.preventDefault(); if (!error) mutation.mutate(); }}>
      <h2>이체</h2>
      <label>출금 계좌
        <select value={from} onChange={(e) => setFrom(e.target.value)}>
          <option value="">선택하세요</option>
          {accounts.map((a) => (
            <option key={a.id} value={a.accountNumber}>
              {a.accountNumber} (잔액 {a.balance.toLocaleString("ko-KR")}원)
            </option>
          ))}
        </select>
      </label>
      <label>받는 계좌
        <input value={to} onChange={(e) => setTo(e.target.value)} placeholder="110-0000-0000" />
      </label>
      <label>금액
        <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} />
      </label>
      {error && <p className="hint">{error}</p>}
      {mutation.error && <p className="error">{mutation.error.message}</p>}
      <button disabled={!!error || mutation.isPending}>
        {mutation.isPending ? "처리 중..." : "이체하기"}
      </button>
    </form>
  );
}

// 18.2절 입력 검증 함수
function validate(from: string, to: string, amount: string, accounts: Account[]): string | null {
  if (!from) return "출금 계좌를 선택하세요.";
  if (!/^110-\d{4}-\d{4}$/.test(to)) return "받는 계좌는 110-0000-0000 형식입니다.";
  if (from === to) return "같은 계좌로는 이체할 수 없습니다.";
  const n = Number(amount);
  if (!amount || n <= 0) return "금액을 입력하세요.";
  const balance = accounts.find((a) => a.accountNumber === from)?.balance ?? 0;
  if (n > balance) return "잔액보다 큰 금액은 이체할 수 없습니다.";
  return null;
}
