/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      fontSize: {
        base: '18px',
      },
      minHeight: {
        touch: '56px',
      },
      colors: {
        primary: {
          DEFAULT: '#2563eb',
          dark: '#1d4ed8',
        },
        kakao: {
          DEFAULT: '#FEE500',
          text: '#191919',
        },
      },
    },
  },
  plugins: [],
}
