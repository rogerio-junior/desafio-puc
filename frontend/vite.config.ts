import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

// Endereço da API usado pelo proxy de /api (npm run dev e npm run preview)
const apiUrl = process.env.API_URL ?? 'http://localhost:8080';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: apiUrl,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
});
