export interface Symptom {
  keyword: string
  label: string
  icon: string
}

interface Props {
  symptom: Symptom
  selected: boolean
  onToggle: (keyword: string) => void
}

export default function SymptomCard({ symptom, selected, onToggle }: Props) {
  return (
    <button
      onClick={() => onToggle(symptom.keyword)}
      className={`
        relative flex flex-col items-center justify-center gap-2
        rounded-2xl border-2 p-4 min-h-touch w-full
        transition-all active:scale-95
        ${selected
          ? 'border-primary bg-blue-50 text-primary'
          : 'border-gray-200 bg-white text-gray-700'
        }
      `}
    >
      <span className="text-4xl">{symptom.icon}</span>
      <span className="text-base font-semibold">{symptom.label}</span>
      {selected && (
        <span className="absolute top-2 right-2 text-primary text-lg">✓</span>
      )}
    </button>
  )
}
