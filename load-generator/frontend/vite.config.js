import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
    server: {
        port: 3000,
        proxy: {
            '/v1': {
                target: 'http://localhost:10200', // Your Vert.x server
                changeOrigin: true
            }
        }
    },
    build: {
        outDir: '../src/main/resources/dist/',
        emptyOutDir: true
    }
})
