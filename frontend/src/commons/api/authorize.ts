import { HOST } from "../hosts.ts";

const endpoint = {
    authorize: "/authorize",
};

export async function authorize(): Promise<boolean> {
    const token = localStorage.getItem("token");
    if (!token) {
        console.error("No token found in localStorage");
        return false;
    }

    try {
        const response = await fetch(
            HOST.backend_auth + endpoint.authorize + "?role=ADMIN",
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            }
        );

        return response.ok;
    } catch (err) {
        console.error("Authorization failed:", err);
        return false;
    }
}
