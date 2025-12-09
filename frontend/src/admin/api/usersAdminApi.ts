import { HOST } from "../../commons/hosts";
import type {UserDTO} from "../../commons/types.ts";

let cachedUsersAdmin: UserDTO[] | null = null;

const endpoint = {
    register: "/register",
    delete: "/delete",
    update: "/update",
    updateProfile: "/profile"
};

export async function getAllClients(): Promise<UserDTO[] | null> {
    const token = localStorage.getItem("token");

    try {
        const res = await fetch(HOST.backend_user, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (res.ok) {
            const users: UserDTO[] = await res.json();

            cachedUsersAdmin = users;

            console.log(`Cached (memory only) ${users.length} admin users.`);
            return users;
        }

        throw new Error("Service unavailable");
    } catch (err) {
        console.error("Failed to fetch users:", err);

        if (cachedUsersAdmin) {
            console.log(
                `Loaded ${cachedUsersAdmin.length} cached admin users (API error fallback)`
            );
            return cachedUsersAdmin;
        }

        return null;
    }
}

export async function getUserById(id: number): Promise<UserDTO | null> {
    try {
        const token = localStorage.getItem("token");

        const res = await fetch(`${HOST.backend_user}/${id}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });

        return res.ok ? await res.json() : null;
    } catch (err) {
        console.error("Error fetching user by ID:", err);
        return null;
    }
}

export async function waitForUser(id: number): Promise<UserDTO | null> {
    const maxAttempts = 5;
    const delay = (ms: number) => new Promise((res) => setTimeout(res, ms));

    for (let attempt = 0; attempt < maxAttempts; attempt++) {
        const user = await getUserById(id);
        if (user) return user;
        await delay(200);
    }

    return null;
}

export async function createUser(user: UserDTO): Promise<UserDTO | null> {
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(HOST.backend_auth + endpoint.register, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(user),
        });

        if (!response.ok) return null;

        const created = await response.json();

        if (cachedUsersAdmin) {
            cachedUsersAdmin = [...cachedUsersAdmin, created];
        }

        return created;
    } catch (err) {
        console.error("Create user failed:", err);
        return null;
    }
}

export async function updateUser(
    id: number,
    user: UserDTO
): Promise<UserDTO | null> {
    const token = localStorage.getItem("token");

    try {
        const authResponse = await fetch(
            `${HOST.backend_auth}${endpoint.update}/${id}`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    username: user.username,
                    password: user.password,
                    role: user.role
                })
            }
        );

        if (!authResponse.ok) {
            console.error("Auth update failed");
            return null;
        }

        const userResponse = await fetch(
            `${HOST.backend_user}/${id}${endpoint.updateProfile}`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify({
                    age: user.age
                })
            }
        );

        if (!userResponse.ok) {
            console.error("User profile update failed");
            return null;
        }

        const updated = await userResponse.json();

        if (cachedUsersAdmin) {
            cachedUsersAdmin = cachedUsersAdmin.map((u) =>
                u.id === id ? updated : u
            );
        }

        return updated;
    } catch (err) {
        console.error("Update user failed:", err);
        return null;
    }
}

export async function deleteUser(id: number): Promise<boolean> {
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(
            `${HOST.backend_auth}${endpoint.delete}/${id}`,
            {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            }
        );

        if (!response.ok) return false;

        if (cachedUsersAdmin) {
            cachedUsersAdmin = cachedUsersAdmin.filter(u => u.id !== id);
        }

        return true;

    } catch (err) {
        console.error("Delete user failed:", err);
        return false;
    }
}
