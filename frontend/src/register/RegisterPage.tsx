import RegisterForm, {type RegisterFormData } from "./components/RegisterForm";
import { postRegister } from "./api/registerApi";
import {validateAuth} from "../commons/validators/authValidator.ts";

const RegisterPage = () => {
    localStorage.clear();

    const handleRegister = (data: RegisterFormData): void => {
        const validation = validateAuth(data);

        if (!validation.isValid) {
            const msg = Object.entries(validation.errors)
                .map(([field, error]) => `${field}: ${error}`)
                .join("\n");
            alert(`Validation failed:\n${msg}`);
            return;
        }

        postRegister(data, (result, status, err) => {
            if (status === 200 || status === 201) {
                alert("Registration successful!");
                console.log("Success: ", result);
            } else {
                alert("Registration failed!");
                console.error("Error: ", err);
            }
        });
    };

    return <RegisterForm onRegister={handleRegister} />;
};

export default RegisterPage;
