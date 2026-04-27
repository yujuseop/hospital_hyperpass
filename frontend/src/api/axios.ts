import axios from "axios";

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "/api",
  headers: { "Content-Type": "application/json" },
});

instance.interceptors.request.use((config) => {
  const token = sessionStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

instance.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      // 관리자 경로에서는 관리자 로그인으로, 그 외에는 환자 인증 화면으로 보낸다.
      const isAdminPath = window.location.pathname.startsWith("/admin");
      sessionStorage.removeItem("token");
      sessionStorage.removeItem("patientId");
      sessionStorage.removeItem("staffName");
      window.location.href = isAdminPath ? "/admin" : "/auth";
    }
    return Promise.reject(error);
  },
);

export default instance;
