import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { api } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const mutation = useMutation({
    mutationFn: () => api<{ accessToken: string; name: string }>("/api/auth/login", {
      method: "POST", body: JSON.stringify({ username, password }),
    }),
    onSuccess: (res) => { login(res.accessToken, res.name); navigate("/"); },
  });
  return (
    <form onSubmit={(e) => { e.preventDefault(); mutation.mutate(); }}>
      <h2>미니뱅크 로그인</h2>
      <label>아이디
        <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="아이디" />
      </label>
      <label>비밀번호
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호" />
      </label>
      {mutation.error && <p className="error">{mutation.error.message}</p>}
      <button disabled={mutation.isPending}>로그인</button>
    </form>
  );
}
