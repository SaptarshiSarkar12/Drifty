/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./app/**/*.{js,ts,jsx,tsx}",
  "./pages/**/*.{js,ts,jsx,tsx}",
  "./components/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors:{
        'top':'#3697e1',
        'var':'#1a2035',
        'btn-color':'#79dae8',
        'bottom':'#e8f9fd',
        'about':'#79dae8',
        'input':'#2d2d2d'
      },
      screens:{
        'xs':'350px'
      }
    },
  },
  plugins: [],
}
