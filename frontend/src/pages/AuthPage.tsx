import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { usePatientAuth } from "../hooks/usePatientAuth";

const RRN_REGEX = /^\d{6}-\d{7}$/;
const PHONE_REGEX = /^01[0-9]-\d{3,4}-\d{4}$/;

function formatRrn(value: string): string {
  const digits = value.replace(/\D/g, "").slice(0, 13);
  if (digits.length <= 6) return digits;
  return `${digits.slice(0, 6)}-${digits.slice(6)}`;
}

function formatPhone(value: string): string {
  const digits = value.replace(/\D/g, "").slice(0, 11);
  if (digits.length < 4) return digits;
  if (digits.length < 8) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  return `${digits.slice(0, 3)}-${digits.slice(3, digits.length === 10 ? 6 : 7)}-${digits.slice(
    digits.length === 10 ? 6 : 7,
  )}`;
}

export default function AuthPage() {
  const navigate = useNavigate();
  const authMutation = usePatientAuth();
  const [name, setName] = useState("");
  const [rrn, setRrn] = useState("");
  const [address, setAddress] = useState("");
  const [phone, setPhone] = useState("");
  const [formError, setFormError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);

    if (!RRN_REGEX.test(rrn)) {
      setFormError("주민등록번호 형식은 000000-0000000 이어야 합니다.");
      return;
    }

    if (!PHONE_REGEX.test(phone)) {
      setFormError("휴대폰번호 형식은 010-1234-5678 이어야 합니다.");
      return;
    }

    try {
      await authMutation.mutateAsync({ name, rrn, address, phone });
      navigate("/symptoms");
    } catch {
      // error는 authMutation.isError / authMutation.error 로 처리
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 gap-8">
      <div className="text-center space-y-2">
        <p className="text-5xl">🔐</p>
        <h1 className="text-2xl font-extrabold text-gray-900">본인 확인</h1>
        <p className="text-gray-500 text-base">
          이름, 주민등록번호, 주소, 휴대폰번호를 입력해 주세요
        </p>
      </div>

      <form onSubmit={handleSubmit} className="card w-full max-w-sm space-y-4">
        <label className="block space-y-1">
          <span className="text-sm font-semibold text-gray-700">이름</span>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="홍길동"
            required
            className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base focus:outline-none focus:border-primary"
          />
        </label>

        <label className="block space-y-1">
          <span className="text-sm font-semibold text-gray-700">
            주민등록번호
          </span>
          <input
            type="text"
            value={rrn}
            onChange={(e) => setRrn(formatRrn(e.target.value))}
            placeholder="900101-1234567"
            pattern="^\d{6}-\d{7}$"
            required
            className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base focus:outline-none focus:border-primary"
          />
        </label>

        <label className="block space-y-1">
          <span className="text-sm font-semibold text-gray-700">주소</span>
          <input
            type="text"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="서울특별시 강남구 테헤란로 123"
            required
            className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base focus:outline-none focus:border-primary"
          />
        </label>

        <label className="block space-y-1">
          <span className="text-sm font-semibold text-gray-700">
            휴대폰번호
          </span>
          <input
            type="tel"
            value={phone}
            onChange={(e) => setPhone(formatPhone(e.target.value))}
            placeholder="010-1234-5678"
            pattern="^01[0-9]-\d{3,4}-\d{4}$"
            required
            className="w-full border border-gray-300 rounded-xl px-4 py-3 text-base focus:outline-none focus:border-primary"
          />
        </label>

        <button
          type="submit"
          disabled={authMutation.isPending}
          className="btn-primary disabled:opacity-50"
        >
          {authMutation.isPending ? "확인 중…" : "확인"}
        </button>

        {authMutation.isError && (
          <p className="text-red-600 text-sm bg-red-50 border border-red-200 rounded-xl px-3 py-2">
            인증에 실패했습니다. 다시 시도해 주세요.
          </p>
        )}
        {formError && (
          <p className="text-red-600 text-sm bg-red-50 border border-red-200 rounded-xl px-3 py-2">
            {formError}
          </p>
        )}
      </form>
    </div>
  );
}
