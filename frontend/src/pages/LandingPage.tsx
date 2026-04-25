import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

export default function LandingPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const id = searchParams.get("kioskId") ?? "1";
    sessionStorage.setItem("kioskId", id);
  }, [searchParams]);

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 gap-8">
      <div className="text-center space-y-3">
        <p className="text-6xl">🏥</p>
        <h1 className="text-2xl font-extrabold text-gray-900">하이패스 접수</h1>
        <p className="text-gray-500">본인 확인 후 빠르게 접수할 수 있습니다</p>
      </div>

      <div className="w-full max-w-sm">
        <button className="btn-primary" onClick={() => navigate("/auth")}>
          접수 시작하기
        </button>
      </div>
    </div>
  );
}
