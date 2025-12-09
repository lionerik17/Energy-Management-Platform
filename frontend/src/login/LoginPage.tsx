import LoginForm, {type LoginFormData } from "./components/LoginForm";
import {postLogin} from "./api/loginApi.ts";
import {useNavigate} from "react-router-dom";

const LoginPage = () => {
    const navigate = useNavigate();
    localStorage.clear();

    const handleLogin = (data: LoginFormData): void => {
        postLogin(data, (result, status, err) => {
            if (status === 200 || status === 201) {
                const token = result?.accessToken;
                const role = result?.role;

                if (token) {
                    localStorage.setItem("token", token);
                }

                if (role === "CLIENT") {
                    navigate("/client");
                } else if (role == "ADMIN") {
                    navigate("/admin")
                }

                console.log("Success: ", result);
            } else {
                alert("Login failed!");
                console.error("Error: ", err);
            }
        });
    };

    return <LoginForm onLogin={handleLogin} />;
};

export default LoginPage;
