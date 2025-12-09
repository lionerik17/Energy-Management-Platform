export type ApiCallback<T = unknown> = (data: T | null, status: number, error: any) => void;

export function performRequest<T = unknown>(request: Request, callback: ApiCallback<T>): void {
    fetch(request)
        .then(async (response) => {
            const contentType = response.headers.get("Content-Type");
            const isJson = contentType && contentType.includes("application/json");
            const data = isJson ? await response.json().catch(() => null) : null;

            if (response.ok) {
                callback(data, response.status, null);
            } else {
                callback(null, response.status, data || "Request failed");
            }
        })
        .catch((err: any) => {
            console.error("Error: ", err);
            callback(null, 0, err);
        });
}

const RestApiClient = { performRequest };
export default RestApiClient;