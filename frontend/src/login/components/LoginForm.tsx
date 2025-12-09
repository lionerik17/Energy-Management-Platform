import { useState, type ChangeEvent, type FormEvent } from "react";
import { Link } from "react-router-dom";
import styles from "../../commons/AuthForm.module.css";

export interface LoginFormData {
    username: string;
    password: string;
}

interface LoginFormProps {
    onLogin: (data: LoginFormData) => void;
}

const LoginForm = ({ onLogin }: LoginFormProps) => {
    const [formData, setFormData] = useState<LoginFormData>({
        username: "",
        password: "",
    });

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        onLogin(formData);
    };

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.formContainer}>
                <h2 className={styles.title}>Login</h2>

                <form onSubmit={handleSubmit} className={styles.form}>
                    <div>
                        <label className={styles.label}>Username</label>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            placeholder="Enter username"
                            required
                            className={styles.input}
                        />
                    </div>

                    <div>
                        <label className={styles.label}>Password</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Enter password"
                            required
                            className={styles.input}
                        />
                    </div>

                    <button type="submit" className={styles.button}>
                        Sign In
                    </button>
                </form>

                <p className={styles.footerText}>
                    Don’t have an account?{" "}
                    <a href="#" className={styles.link}>
                        <Link to="/register" className={styles.link}>
                            Register
                        </Link>
                    </a>
                </p>
            </div>
        </div>
    );
};

export default LoginForm;
