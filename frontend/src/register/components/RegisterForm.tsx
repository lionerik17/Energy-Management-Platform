import { useState, type ChangeEvent, type FormEvent } from "react";
import { Link } from "react-router-dom";
import styles from "../../commons/AuthForm.module.css";

export interface RegisterFormData {
    username: string;
    password: string;
    age: number;
}

interface RegisterFormProps {
    onRegister: (data: RegisterFormData) => void;
}

const RegisterForm = ({ onRegister }: RegisterFormProps) => {
    const [formData, setFormData] = useState<RegisterFormData>({
        username: "",
        password: "",
        age: 1,
    });

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        onRegister(formData);
    };

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.formContainer}>
                <h2 className={styles.title}>Create an Account</h2>

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

                    <div>
                        <label className={styles.label}>Age</label>
                        <input
                            type="number"
                            name="age"
                            min="1"
                            step="any"
                            value={formData.age && formData.age  > 0 ? formData.age : 1}
                            onChange={(e) => {
                                const value = Math.max(1, Number(e.target.value));
                                setFormData({...formData, age: value});
                            }}
                            onKeyDown={(e) => {
                                if (e.key === "-" || e.key === "e") e.preventDefault();
                            }}
                            className={styles.input}
                            placeholder="Enter age"
                            required
                        />
                    </div>

                    <button type="submit" className={styles.button}>
                        Register
                    </button>
                </form>

                <p className={styles.footerText}>
                    Already have an account?{" "}
                    <Link to="/login" className={styles.link}>
                        Login
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterForm;
