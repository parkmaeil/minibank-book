import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import AccountsPage from "./pages/AccountsPage";
import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AccountsPage />
  </StrictMode>
);
