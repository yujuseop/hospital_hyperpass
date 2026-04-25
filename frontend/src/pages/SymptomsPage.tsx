import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import SymptomCard from "../components/SymptomCard";
import { usePreCheckIn } from "../hooks/usePreCheckIn";
import type { Symptom } from "../components/SymptomCard";

const SYMPTOMS: Symptom[] = [
  { keyword: "기침", label: "기침", icon: "🫁" },
  { keyword: "발열", label: "발열", icon: "🌡️" },
  { keyword: "복통", label: "복통", icon: "🤢" },
  { keyword: "골절", label: "골절", icon: "🦴" },
  { keyword: "관절통", label: "관절통", icon: "🦵" },
  { keyword: "피부발진", label: "피부발진", icon: "🩹" },
  { keyword: "눈충혈", label: "눈충혈", icon: "👁️" },
  { keyword: "이통", label: "이통", icon: "👂" },
  { keyword: "두통", label: "두통", icon: "🧠" },
  { keyword: "흉통", label: "흉통", icon: "❤️" },
];

export default function SymptomsPage() {
  const navigate = useNavigate();
  const preCheckInMutation = usePreCheckIn();
  const [selected, setSelected] = useState<string[]>([]);
  const [painArea, setPainArea] = useState("");
  const [painLevel, setPainLevel] = useState(0);
  const [startedAtText, setStartedAtText] = useState("");
  const [freeText, setFreeText] = useState("");

  const canSubmit = useMemo(() => selected.length > 0, [selected]);

  const toggle = (keyword: string) =>
    setSelected((prev) =>
      prev.includes(keyword)
        ? prev.filter((k) => k !== keyword)
        : [...prev, keyword],
    );

  const handleSubmit = async () => {
    await preCheckInMutation.mutateAsync({
      mainSymptom: selected[0],
      symptomKeywords: selected,
      painArea: painArea.trim() || undefined,
      painLevel,
      startedAtText: startedAtText.trim() || undefined,
      freeText: freeText.trim() || undefined,
    });
  };

  if (preCheckInMutation.data) {
    const result = preCheckInMutation.data;
    return (
      <div className="min-h-screen flex items-center justify-center p-6">
        <div className="card w-full max-w-sm space-y-4 text-center">
          <p className="text-4xl">✅</p>
          <h1 className="text-2xl font-extrabold text-gray-900">
            문진 제출 완료
          </h1>
          <p className="text-gray-600">{result.message}</p>
          <p className="text-sm text-gray-400">
            접수번호: {result.receptionId}
          </p>
          <button className="btn-primary" onClick={() => navigate("/")}>
            처음으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col p-6 gap-6">
      <div className="text-center space-y-1 pt-4">
        <h1 className="text-2xl font-extrabold text-gray-900">
          사전 문진 작성
        </h1>
        <p className="text-gray-500">
          증상을 선택하고 상세 정보를 입력해 주세요 (복수 선택 가능)
        </p>
      </div>

      <div className="grid grid-cols-2 gap-3">
        {SYMPTOMS.map((symptom) => (
          <SymptomCard
            key={symptom.keyword}
            symptom={symptom}
            selected={selected.includes(symptom.keyword)}
            onToggle={toggle}
          />
        ))}
      </div>

      <div className="card space-y-3">
        <p className="text-sm font-semibold text-gray-500">통증·증상 상세</p>

        <label className="block space-y-1">
          <span className="text-sm text-gray-600">통증 부위</span>
          <input
            value={painArea}
            onChange={(e) => setPainArea(e.target.value)}
            className="w-full border border-gray-200 rounded-xl px-3 py-2 text-base outline-none focus:ring-2 focus:ring-primary/30"
            placeholder="예: 오른쪽 아랫배, 허리"
            maxLength={100}
          />
        </label>

        <label className="block space-y-1">
          <span className="text-sm text-gray-600">통증 강도 (0~10)</span>
          <input
            type="range"
            min={0}
            max={10}
            value={painLevel}
            onChange={(e) => setPainLevel(Number(e.target.value))}
            className="w-full"
          />
          <p className="text-sm text-gray-500">현재 통증 강도: {painLevel}</p>
        </label>

        <label className="block space-y-1">
          <span className="text-sm text-gray-600">증상 시작 시점</span>
          <input
            value={startedAtText}
            onChange={(e) => setStartedAtText(e.target.value)}
            className="w-full border border-gray-200 rounded-xl px-3 py-2 text-base outline-none focus:ring-2 focus:ring-primary/30"
            placeholder="예: 어제 저녁부터, 3일 전부터"
            maxLength={100}
          />
        </label>

        <label className="block space-y-1">
          <span className="text-sm text-gray-600">추가로 전달할 내용</span>
          <textarea
            value={freeText}
            onChange={(e) => setFreeText(e.target.value)}
            className="w-full min-h-24 border border-gray-200 rounded-xl px-3 py-2 text-base outline-none focus:ring-2 focus:ring-primary/30 resize-y"
            placeholder="현재 가장 불편한 점이나 전달하고 싶은 내용을 적어 주세요."
            maxLength={500}
          />
        </label>
      </div>

      {preCheckInMutation.isError && (
        <div className="bg-red-50 border border-red-200 rounded-xl px-4 py-3">
          <p className="text-red-600 text-base">
            문진 제출 중 오류가 발생했습니다. 다시 시도해 주세요.
          </p>
        </div>
      )}

      <div className="sticky bottom-6">
        <button
          className="btn-primary disabled:opacity-40"
          disabled={!canSubmit || preCheckInMutation.isPending}
          onClick={handleSubmit}
        >
          {preCheckInMutation.isPending
            ? "문진 제출 중…"
            : `문진 제출하기${selected.length > 0 ? ` (${selected.length}개 선택)` : ""}`}
        </button>
        <p className="text-xs text-gray-400 mt-2 text-center">
          제출 후 원무과 확인이 완료되면 접수가 시작됩니다.
        </p>
      </div>
    </div>
  );
}
