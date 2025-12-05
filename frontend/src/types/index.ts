// ================================
// API Response Types
// ================================

export interface ResponseObject<T> {
  status: string;
  message: string;
  data: T;
}

// ================================
// DTO Types (matching backend)
// ================================

export interface AIFeedbackRequestDTO {
  studentId: number;
  questionId: number;
  questionText: string;
  studentAnswer: string;
  correctAnswer: string;
  topic: string;
  difficulty: string;
  subject: string;
}

export interface AIFeedbackDTO {
  id: number;
  studentId: number;
  questionId: number;
  feedbackText: string;
  hint: string;
}

export interface RecommendationDTO {
  nextTopic: string;
  explanation: string;
}

// ================================
// AI Learning Bot Types (1.3.5 Personalized Learning)
// ================================

export interface ChatRequestDTO {
  studentId: number;
  message: string;
  learningMaterialContext?: string;
  currentTopic?: string;
  subject?: string;
}

export interface ChatResponseDTO {
  studentId: number;
  userMessage: string;
  aiResponse: string;
  suggestedMaterials?: string;
  timestamp: string;
}

export interface StudentProfileDTO {
  studentId: number;
  totalAttempts: number;
  correctAnswers: number;
  accuracy: number;
  strengths: string;
  weaknesses: string;
  recommendedDifficulty: string;
  recommendedNextTopic: string;
}
