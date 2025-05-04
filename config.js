const ENV = "development";

const configs = {
    development: {
        apiBaseUrl: "http://localhost:8080/api"
    },
    production: {
        apiBaseUrl: ""
    }
};

export const config = configs[ENV];