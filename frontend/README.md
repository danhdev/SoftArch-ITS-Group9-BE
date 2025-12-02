# AI Feedback Demo - Frontend

Frontend React TypeScript cho demo AI Feedback Service backend.

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ 
- npm hoặc yarn
- Backend đang chạy ở `http://localhost:8080`

### Installation

```bash
# Di chuyển vào thư mục frontend
cd frontend

# Cài đặt dependencies
npm install

# Chạy development server
npm run dev
```

Frontend sẽ chạy ở `http://localhost:3000`

## 📁 Project Structure

```
frontend/
├── public/
│   └── vite.svg
├── src/
│   ├── api/
│   │   └── feedbackApi.ts    # API client functions
│   ├── types/
│   │   └── index.ts          # TypeScript interfaces
│   ├── App.tsx               # Main App component
│   ├── main.tsx              # Entry point
│   ├── index.css             # Global styles
│   └── vite-env.d.ts
├── package.json
├── tsconfig.json
├── tsconfig.node.json
└── vite.config.ts
```

## 🔗 API Endpoints

Frontend kết nối với các API sau:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/ai-feedback/generate` | POST | Generate AI feedback |
| `/api/ai-feedback/history/{studentId}` | GET | Get feedback history |
| `/api/ai-feedback/latest/{studentId}` | GET | Get latest feedback |
| `/api/ai-feedback/recommendations/{studentId}` | GET | Get recommendations |

## 🎨 Features

- **Generate Feedback Form**: Tạo feedback mới cho student submission
- **History Tab**: Xem tất cả feedback của một student
- **Latest Tab**: Xem feedback gần nhất
- **Recommendations Tab**: Xem gợi ý học tập từ AI

## 🛠️ Development

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## ⚙️ Configuration

Vite proxy được cấu hình trong `vite.config.ts`:

```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  }
}
```

Điều này cho phép frontend gọi `/api/*` và được proxy đến backend.
