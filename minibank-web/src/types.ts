// 백엔드 AccountResponseDto(부록 G.5)와 같은 모양
export interface Account {
  id: number;
  accountNumber: string;
  ownerName: string;
  balance: number;
  dailyLimit: number;
  openedAt: string;          // LocalDateTime은 JSON에서 "2026-10-30T14:02:11" 같은 문자열
}

// TransactionResponseDto
export interface Transaction {
  id: number;
  type: 'WITHDRAW' | 'DEPOSIT';
  amount: number;
  balanceAfter: number;
  transactedAt: string;
  description: string;
}

// Spring Data의 Page<T>가 JSON으로 바뀐 모양(필요한 필드만)
export interface Page<T> {
  content: T[];
  totalPages: number;
  number: number;            // 현재 페이지(0부터)
}
