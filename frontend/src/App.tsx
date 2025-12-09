import RegisterPage from "./register/RegisterPage";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import LoginPage from "./login/LoginPage.tsx";
import ClientPage from "./client/ClientPage.tsx";
import AdminPage from "./admin/AdminPage.tsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/client/*" element={<ClientPage />} />
                <Route path="/admin/*" element={<AdminPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
