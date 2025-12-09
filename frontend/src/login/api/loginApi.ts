import { HOST } from "../../commons/hosts";
import RestApiClient, {type ApiCallback } from "../../commons/api/rest-client";
import type {AuthResponse} from "../../commons/types.ts";

const endpoint = {
    login: "/login",
};

export interface LoginUser {
    username: string;
    password: string;
}

export function postLogin(user: LoginUser, callback: ApiCallback<AuthResponse>): void {
    const request = new Request(HOST.backend_auth + endpoint.login, {
        method: "POST",
        headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
    });

    console.log("Login request to:", request.url);
    RestApiClient.performRequest<AuthResponse>(request, callback);
}