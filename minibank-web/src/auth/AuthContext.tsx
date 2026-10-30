import { createContext, useContext, useState, type ReactNode } from "react";

interface AuthState {
  token: string | null;
  name: string | null;
  login: (token: string, name: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState(() => sessionStorage.getItem("token"));
  const [name, setName] = useState(() => sessionStorage.getItem("name"));

  const login = (t: string, n: string) => {
    sessionStorage.setItem("token", t); sessionStorage.setItem("name", n);
    setToken(t); setName(n);
  };
  const logout = () => {
    sessionStorage.clear(); setToken(null); setName(null);
  };
  return <AuthContext.Provider value={{ token, name, login, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("AuthProvider 안에서만 사용할 수 있습니다.");
  return ctx;
}
