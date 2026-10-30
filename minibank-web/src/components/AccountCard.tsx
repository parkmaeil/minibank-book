import type { Account } from "../types";

const won = new Intl.NumberFormat("ko-KR");     // 30000 → "30,000"

interface Props {
  account: Account;
  onShowHistory: (accountNumber: string) => void;   // 부모가 넘겨주는 이벤트 함수
}

export default function AccountCard({ account, onShowHistory }: Props) {
  return (
    <div className="card">
      <div className="card-number">{account.accountNumber}</div>
      <div className="card-balance">{won.format(account.balance)}원</div>
      <button onClick={() => onShowHistory(account.accountNumber)}>거래내역</button>
    </div>
  );
}
