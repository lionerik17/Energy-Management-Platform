import { HOST } from "../../commons/hosts";
import RestApiClient, {type ApiCallback } from "../../commons/api/rest-client";

const endpoint = {
    register: "/register",
};

export interface RegisterUser {
    username: string;
    password: string;
    age: number;
}

export function postRegister(user: RegisterUser, callback: ApiCallback): void {
    const request = new Request(HOST.backend_auth + endpoint.register, {
        method: "POST",
        headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
    });

    console.log("Register request to:", request.url);
    RestApiClient.performRequest(request, callback);
}