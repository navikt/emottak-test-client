/** @type {import('postcss-load-config').Config} */
const config = {
  plugins: {
    tailwindcss: {},
  },
  logging: {
    fetches: {
      fullUrl: true,
    },
  },
};

export default config;
