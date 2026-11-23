import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": "http://localhost:8080",    // /api로 시작하는 요청은 Spring Boot로 전달
    },
  },
});
