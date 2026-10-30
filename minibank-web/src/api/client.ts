export class ApiError extends Error {
  // Vite 템플릿의 tsconfig는 erasableSyntaxOnly가 켜져 있어
  // constructor(public status: number) 같은 매개변수 속성을 쓸 수 없다. 필드를 따로 선언한다.
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const res = await fetch(path, {
    ...options,
    headers: { "Content-Type": "application/json", ...options.headers },
  });

  if (!res.ok) {
    // 백엔드 GlobalExceptionHandler의 {"message": "..."} 형식을 읽는다
    const body = await res.json().catch(() => ({}));
    throw new ApiError(res.status, body.message ?? `요청 실패 (${res.status})`);
  }
  // 200 OK에 본문이 없는 응답(이체 등) 처리
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}
