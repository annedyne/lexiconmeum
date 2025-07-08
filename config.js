const ENV = "development";

const configs = {
    development: {
        apiBaseUrl: "http://localhost:8085/api/v1"
    },
    production: {
        apiBaseUrl: "http://localhost:8085/api/v1"
    }
};

export const config = configs[ENV];