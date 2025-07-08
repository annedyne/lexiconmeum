const ENV = window.location.hostname === "localhost" ? "development" : "production";

const configs = {
    development: {
        apiBaseUrl: "http://localhost:8085/api/v1"
    },
    production: {
        apiBaseUrl: "/api/v1"
    }
};

export const config = configs[ENV];