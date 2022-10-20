/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    extend: {
      colors: {
        'dark': '#0f0f0f',
        'light_dark': '#201f1f',
        'light': '#f9f4f4',
        'primary': '#d7fb5a',
        'secondary': '#cd35eb',
      }
    },
  },
  plugins: []
};
