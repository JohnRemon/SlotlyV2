import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

const API = axios.create({
    baseURL: BASE_URL,
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
    },
    xsrfCookieName: "XSRF_TOKEN",
    xsrfHeaderName: "X-XSRF_TOKEN",
});

export default API;
