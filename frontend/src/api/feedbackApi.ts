import axios from 'axios';
import type { 
  ResponseObject, 
  AIFeedbackRequestDTO, 
  AIFeedbackDTO, 
  RecommendationDTO,
  ChatRequestDTO,
  ChatResponseDTO,
  StudentProfileDTO
} from '../types';

// ================================
// API Client Configuration
// ================================

const api = axios.create({
  baseURL: '/api/ai-feedback',
  headers: {
    'Content-Type': 'application/json',
  },
});

const learningBotApi = axios.create({
  baseURL: '/api/ai-learning-bot',
  headers: {
    'Content-Type': 'application/json',
  },
});

// ================================
// AI Feedback API Functions
// ================================

/**
 * Generate AI feedback for a student submission
 */
export const generateFeedback = async (
  request: AIFeedbackRequestDTO
): Promise<ResponseObject<AIFeedbackDTO>> => {
  const response = await api.post<ResponseObject<AIFeedbackDTO>>('/generate', request);
  return response.data;
};

/**
 * Get feedback history for a student
 */
export const getFeedbackHistory = async (
  studentId: number
): Promise<ResponseObject<AIFeedbackDTO[]>> => {
  const response = await api.get<ResponseObject<AIFeedbackDTO[]>>(`/history/${studentId}`);
  return response.data;
};

/**
 * Get latest feedback for a student
 */
export const getLatestFeedback = async (
  studentId: number
): Promise<ResponseObject<AIFeedbackDTO>> => {
  const response = await api.get<ResponseObject<AIFeedbackDTO>>(`/latest/${studentId}`);
  return response.data;
};

/**
 * Get learning recommendations for a student
 */
export const getRecommendations = async (
  studentId: number
): Promise<ResponseObject<RecommendationDTO[]>> => {
  const response = await api.get<ResponseObject<RecommendationDTO[]>>(`/recommendations/${studentId}`);
  return response.data;
};

// ================================
// AI Learning Bot API Functions (1.3.5 Personalized Learning)
// ================================

/**
 * Chat with AI Learning Bot
 */
export const chatWithBot = async (
  request: ChatRequestDTO
): Promise<ResponseObject<ChatResponseDTO>> => {
  const response = await learningBotApi.post<ResponseObject<ChatResponseDTO>>('/chat', request);
  return response.data;
};

/**
 * Get student learning profile
 */
export const getStudentProfile = async (
  studentId: number
): Promise<ResponseObject<StudentProfileDTO>> => {
  const response = await learningBotApi.get<ResponseObject<StudentProfileDTO>>(`/profile/${studentId}`);
  return response.data;
};

/**
 * Get suggested materials for a student
 */
export const getSuggestedMaterials = async (
  studentId: number,
  topic: string = 'Tổng hợp'
): Promise<ResponseObject<string>> => {
  const response = await learningBotApi.get<ResponseObject<string>>(
    `/materials/${studentId}?topic=${encodeURIComponent(topic)}`
  );
  return response.data;
};

export default api;
