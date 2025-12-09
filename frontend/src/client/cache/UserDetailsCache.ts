import type { UserDetails } from "../api/userApi.ts";

class UserDetailsCache {
    private user: UserDetails | null = null;

    set(user: UserDetails) {
        this.user = structuredClone(user);
    }

    get(): UserDetails | null {
        return this.user ? structuredClone(this.user) : null;
    }

    clear() {
        this.user = null;
    }
}

export const userDetailsCache = new UserDetailsCache();
