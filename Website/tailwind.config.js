/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,md,mdx}",
    "./pages/**/*.{js,jsx,ts,tsx,md,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        top: "#288fe6",
        var: "#1a2035",
        "btn-color": "#79dae8",
        bottom: "#e8f9fd",
        about: "#79dae8",
        input: "#2d2d2d",
        dbtn: "#1c0099",
        "hashnode-color": "#2962FF",
        "github-color": "#333333",
        "twitter-color": "#1DA1F2",
        "linkedin-color": "#0A66C2",
      },
      screens: {
        xs: "280px",
      },
    },
  },
  plugins: [],
};
