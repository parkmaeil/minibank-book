import { useState } from "react";
import { useParams } from "react-router";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { api } from "../api/client";
import type { Page, Transaction } from "../types";

const won = new Intl.NumberFormat("ko-KR");
const dt = new Intl.DateTimeFormat("ko-KR", { dateStyle: "short", timeStyle: "short" });

export default function HistoryPage() {
  const { accountNumber } = useParams();          // 주소의 :accountNumber
  const [page, setPage] = useState(0);

  const { data, isPending, isError, error } = useQuery({
    queryKey: ["history", accountNumber, page],    // 페이지마다 별도 캐시
    queryFn: () => api<Page<Transaction>>(
      `/api/me/accounts/${accountNumber}/transactions?page=${page}&size=10`),
    placeholderData: keepPreviousData,             // 다음 페이지 로딩 중 이전 목록 유지
  });

  if (isPending) return <p>불러오는 중...</p>;
  if (isError) return <p className="error">{error.message}</p>;

  return (
    <section>
      <h2>{accountNumber} 거래내역</h2>
      <table>
        <thead><tr><th>일시</th><th>구분</th><th>금액</th><th>거래후 잔액</th><th>메모</th></tr></thead>
        <tbody>
          {data.content.map((t) => (
            <tr key={t.id}>
              <td>{dt.format(new Date(t.transactedAt))}</td>
              <td className={t.type === "DEPOSIT" ? "plus" : "minus"}>
                {t.type === "DEPOSIT" ? "입금" : "출금"}
              </td>
              <td className="num">{won.format(t.amount)}</td>
              <td className="num">{won.format(t.balanceAfter)}</td>
              <td>{t.description}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="pager">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>◀ 이전</button>
        <span>{data.number + 1} / {Math.max(data.totalPages, 1)}</span>
        <button disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>다음 ▶</button>
      </div>
    </section>
  );
}
