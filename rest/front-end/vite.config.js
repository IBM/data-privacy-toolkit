import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// The application is served by the Spring Boot back-end (static resources) under a relative base,
// and talks to the REST API with relative URLs (api/...).
export default defineConfig({
  base: './',
  plugins: [react()],
  server: {
    host: '127.0.0.1',
    port: 3000,
    proxy: {
      '/api': 'http://127.0.0.1:8080'
    }
  },
  build: {
    outDir: 'build'
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler',
        quietDeps: true
      }
    }
  },
  test: {
    environment: 'jsdom',
    globals: true
  }
})
