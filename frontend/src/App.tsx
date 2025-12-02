import { useState } from 'react';
import type { 
  AIFeedbackRequestDTO, 
  AIFeedbackDTO, 
  RecommendationDTO,
  ChatRequestDTO,
  ChatResponseDTO,
  StudentProfileDTO
} from './types';
import { 
  generateFeedback, 
  getFeedbackHistory, 
  getLatestFeedback, 
  getRecommendations,
  chatWithBot,
  getStudentProfile,
  getSuggestedMaterials
} from './api/feedbackApi';

// ================================
// Main App Component
// ================================

function App() {
  const [activeSection, setActiveSection] = useState<'feedback' | 'chatbot' | 'profile'>('feedback');

  return (
    <div className="app">
      <header className="app-header">
        <h1>🤖 AI Feedback Service Demo</h1>
        <p>Intelligent Tutoring System - Personalized Learning</p>
        
        {/* Navigation */}
        <nav className="main-nav">
          <button 
            className={`nav-btn ${activeSection === 'feedback' ? 'active' : ''}`}
            onClick={() => setActiveSection('feedback')}
          >
            📝 Feedback
          </button>
          <button 
            className={`nav-btn ${activeSection === 'chatbot' ? 'active' : ''}`}
            onClick={() => setActiveSection('chatbot')}
          >
            💬 AI Chat Bot
          </button>
          <button 
            className={`nav-btn ${activeSection === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveSection('profile')}
          >
            📊 Profile
          </button>
        </nav>
      </header>
      
      <main className="main-content">
        {activeSection === 'feedback' && (
          <>
            <FeedbackForm />
            <FeedbackHistory />
          </>
        )}
        {activeSection === 'chatbot' && (
          <AILearningBot />
        )}
        {activeSection === 'profile' && (
          <StudentProfileSection />
        )}
      </main>
    </div>
  );
}

// ================================
// Feedback Form Component
// ================================

function FeedbackForm() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [feedback, setFeedback] = useState<AIFeedbackDTO | null>(null);
  
  const [formData, setFormData] = useState<AIFeedbackRequestDTO>({
    studentId: 1,
    questionId: 101,
    questionText: '2 + 2 = ?',
    studentAnswer: '5',
    correctAnswer: '4',
    topic: 'Phép cộng cơ bản',
    difficulty: 'easy',
    subject: 'Toán học'
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'studentId' || name === 'questionId' ? Number(value) : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setFeedback(null);

    try {
      const response = await generateFeedback(formData);
      setFeedback(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to generate feedback');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">
      <h2>📝 Generate AI Feedback</h2>
      
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label>Student ID</label>
            <input
              type="number"
              name="studentId"
              value={formData.studentId}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label>Question ID</label>
            <input
              type="number"
              name="questionId"
              value={formData.questionId}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label>Question Text</label>
          <textarea
            name="questionText"
            value={formData.questionText}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Student Answer</label>
            <input
              type="text"
              name="studentAnswer"
              value={formData.studentAnswer}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label>Correct Answer</label>
            <input
              type="text"
              name="correctAnswer"
              value={formData.correctAnswer}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Topic</label>
            <input
              type="text"
              name="topic"
              value={formData.topic}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label>Subject</label>
            <input
              type="text"
              name="subject"
              value={formData.subject}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label>Difficulty</label>
          <select name="difficulty" value={formData.difficulty} onChange={handleChange}>
            <option value="easy">Easy</option>
            <option value="medium">Medium</option>
            <option value="hard">Hard</option>
          </select>
        </div>

        <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
          {loading ? 'Generating...' : '🚀 Generate Feedback'}
        </button>
      </form>

      {error && <div className="error">❌ {error}</div>}

      {feedback && (
        <div className="feedback-result">
          <h3>✨ AI Generated Feedback</h3>
          <p className="feedback-text">{feedback.feedbackText}</p>
          {feedback.hint && (
            <div className="hint-box">
              <strong>💡 Hint:</strong> {feedback.hint}
            </div>
          )}
          <div style={{ marginTop: '12px', fontSize: '0.85rem', color: '#666' }}>
            <span className="badge badge-info">ID: {feedback.id}</span>
          </div>
        </div>
      )}
    </div>
  );
}

// ================================
// Feedback History Component
// ================================

function FeedbackHistory() {
  const [activeTab, setActiveTab] = useState<'history' | 'latest' | 'recommendations'>('history');
  const [studentId, setStudentId] = useState<number>(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [historyList, setHistoryList] = useState<AIFeedbackDTO[]>([]);
  const [latestFeedback, setLatestFeedback] = useState<AIFeedbackDTO | null>(null);
  const [recommendations, setRecommendations] = useState<RecommendationDTO[]>([]);

  const fetchHistory = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await getFeedbackHistory(studentId);
      setHistoryList(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch history');
    } finally {
      setLoading(false);
    }
  };

  const fetchLatest = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await getLatestFeedback(studentId);
      setLatestFeedback(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch latest feedback');
    } finally {
      setLoading(false);
    }
  };

  const fetchRecommendations = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await getRecommendations(studentId);
      setRecommendations(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch recommendations');
    } finally {
      setLoading(false);
    }
  };

  const handleFetch = () => {
    switch (activeTab) {
      case 'history':
        fetchHistory();
        break;
      case 'latest':
        fetchLatest();
        break;
      case 'recommendations':
        fetchRecommendations();
        break;
    }
  };

  return (
    <div className="card">
      <h2>📊 Student Data</h2>
      
      <div className="tabs">
        <button
          className={`tab ${activeTab === 'history' ? 'active' : ''}`}
          onClick={() => setActiveTab('history')}
        >
          📜 History
        </button>
        <button
          className={`tab ${activeTab === 'latest' ? 'active' : ''}`}
          onClick={() => setActiveTab('latest')}
        >
          🕐 Latest
        </button>
        <button
          className={`tab ${activeTab === 'recommendations' ? 'active' : ''}`}
          onClick={() => setActiveTab('recommendations')}
        >
          💡 Recommendations
        </button>
      </div>

      <div className="history-controls">
        <input
          type="number"
          placeholder="Student ID"
          value={studentId}
          onChange={(e) => setStudentId(Number(e.target.value))}
        />
        <button className="btn btn-primary" onClick={handleFetch} disabled={loading}>
          {loading ? '...' : '🔍 Fetch'}
        </button>
      </div>

      {error && <div className="error">❌ {error}</div>}

      {/* History Tab */}
      {activeTab === 'history' && (
        <div className="history-list">
          {historyList.length === 0 ? (
            <div className="empty-state">
              <p>No feedback history found.</p>
              <p>Click "Fetch" to load data.</p>
            </div>
          ) : (
            historyList.map((item) => (
              <div key={item.id} className="history-item">
                <div className="history-item-header">
                  <span>Question #{item.questionId}</span>
                  <span className="badge badge-info">ID: {item.id}</span>
                </div>
                <p>{item.feedbackText}</p>
                {item.hint && (
                  <p style={{ color: '#856404', fontSize: '0.9rem' }}>
                    💡 {item.hint}
                  </p>
                )}
              </div>
            ))
          )}
        </div>
      )}

      {/* Latest Tab */}
      {activeTab === 'latest' && (
        <div>
          {latestFeedback ? (
            <div className="feedback-result">
              <h3>Latest Feedback</h3>
              <div className="history-item-header">
                <span>Question #{latestFeedback.questionId}</span>
                <span className="badge badge-success">Latest</span>
              </div>
              <p className="feedback-text">{latestFeedback.feedbackText}</p>
              {latestFeedback.hint && (
                <div className="hint-box">
                  <strong>💡 Hint:</strong> {latestFeedback.hint}
                </div>
              )}
            </div>
          ) : (
            <div className="empty-state">
              <p>No latest feedback loaded.</p>
              <p>Click "Fetch" to load data.</p>
            </div>
          )}
        </div>
      )}

      {/* Recommendations Tab */}
      {activeTab === 'recommendations' && (
        <div className="recommendation-list">
          {recommendations.length === 0 ? (
            <div className="empty-state">
              <p>No recommendations loaded.</p>
              <p>Click "Fetch" to load data.</p>
            </div>
          ) : (
            recommendations.map((rec, index) => (
              <div key={index} className="recommendation-item">
                <h4>📚 {rec.nextTopic}</h4>
                <p>{rec.explanation}</p>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}

// ================================
// AI Learning Bot Component (1.3.5 Personalized Learning)
// ================================

function AILearningBot() {
  const [studentId, setStudentId] = useState<number>(1);
  const [message, setMessage] = useState('');
  const [currentTopic, setCurrentTopic] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [chatHistory, setChatHistory] = useState<ChatResponseDTO[]>([]);

  const handleSendMessage = async () => {
    if (!message.trim()) return;
    
    setLoading(true);
    setError(null);

    const request: ChatRequestDTO = {
      studentId,
      message,
      currentTopic: currentTopic || undefined,
    };

    try {
      const response = await chatWithBot(request);
      setChatHistory(prev => [...prev, response.data]);
      setMessage('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Lỗi khi gửi tin nhắn');
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div className="card chat-card">
      <h2>💬 AI Learning Bot</h2>
      <p className="subtitle">Trao đổi trực tiếp với AI để được hỗ trợ học tập</p>

      <div className="chat-settings">
        <div className="form-row">
          <div className="form-group">
            <label>Student ID</label>
            <input
              type="number"
              value={studentId}
              onChange={(e) => setStudentId(Number(e.target.value))}
            />
          </div>
          <div className="form-group">
            <label>Chủ đề đang học (tuỳ chọn)</label>
            <input
              type="text"
              value={currentTopic}
              onChange={(e) => setCurrentTopic(e.target.value)}
              placeholder="VD: Phương trình bậc 2"
            />
          </div>
        </div>
      </div>

      {error && <div className="error">❌ {error}</div>}

      <div className="chat-container">
        {chatHistory.length === 0 ? (
          <div className="empty-state">
            <p>👋 Xin chào! Tôi là AI Learning Bot.</p>
            <p>Hãy đặt câu hỏi về bài học của bạn!</p>
          </div>
        ) : (
          <div className="chat-messages">
            {chatHistory.map((chat, index) => (
              <div key={index} className="chat-message-group">
                <div className="chat-message user-message">
                  <strong>🧑‍🎓 Bạn:</strong>
                  <p>{chat.userMessage}</p>
                </div>
                <div className="chat-message bot-message">
                  <strong>🤖 AI Bot:</strong>
                  <p style={{ whiteSpace: 'pre-wrap' }}>{chat.aiResponse}</p>
                  {chat.suggestedMaterials && (
                    <div className="suggested-materials">
                      <strong>📚 Tài liệu gợi ý:</strong>
                      <p style={{ whiteSpace: 'pre-wrap' }}>{chat.suggestedMaterials}</p>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="chat-input-container">
        <textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="Nhập câu hỏi của bạn..."
          disabled={loading}
          rows={2}
        />
        <button 
          className="btn btn-primary" 
          onClick={handleSendMessage}
          disabled={loading || !message.trim()}
        >
          {loading ? '⏳' : '📤 Gửi'}
        </button>
      </div>
    </div>
  );
}

// ================================
// Student Profile Component (1.3.5 Personalized Learning)
// ================================

function StudentProfileSection() {
  const [studentId, setStudentId] = useState<number>(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [profile, setProfile] = useState<StudentProfileDTO | null>(null);
  const [materials, setMaterials] = useState<string | null>(null);
  const [topic, setTopic] = useState('Tổng hợp');

  const fetchProfile = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await getStudentProfile(studentId);
      setProfile(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Lỗi khi tải hồ sơ');
    } finally {
      setLoading(false);
    }
  };

  const fetchMaterials = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await getSuggestedMaterials(studentId, topic);
      setMaterials(response.data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Lỗi khi tải tài liệu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-section">
      <div className="card">
        <h2>📊 Hồ sơ học tập</h2>
        
        <div className="history-controls">
          <input
            type="number"
            placeholder="Student ID"
            value={studentId}
            onChange={(e) => setStudentId(Number(e.target.value))}
          />
          <button className="btn btn-primary" onClick={fetchProfile} disabled={loading}>
            {loading ? '...' : '🔍 Xem hồ sơ'}
          </button>
        </div>

        {error && <div className="error">❌ {error}</div>}

        {profile && (
          <div className="profile-details">
            <div className="profile-header">
              <h3>Học sinh #{profile.studentId}</h3>
              <span className={`badge ${profile.accuracy >= 60 ? 'badge-success' : 'badge-error'}`}>
                {profile.accuracy.toFixed(1)}% chính xác
              </span>
            </div>

            <div className="stats-grid">
              <div className="stat-item">
                <span className="stat-value">{profile.totalAttempts}</span>
                <span className="stat-label">Tổng bài làm</span>
              </div>
              <div className="stat-item">
                <span className="stat-value">{profile.correctAnswers}</span>
                <span className="stat-label">Số câu đúng</span>
              </div>
              <div className="stat-item">
                <span className="stat-value">{profile.recommendedDifficulty}</span>
                <span className="stat-label">Độ khó đề xuất</span>
              </div>
            </div>

            <div className="profile-section-item">
              <h4>💪 Điểm mạnh</h4>
              <p>{profile.strengths}</p>
            </div>

            <div className="profile-section-item weakness">
              <h4>📈 Cần cải thiện</h4>
              <p>{profile.weaknesses}</p>
            </div>

            <div className="profile-section-item recommendation">
              <h4>🎯 Chủ đề tiếp theo</h4>
              <p>{profile.recommendedNextTopic}</p>
            </div>
          </div>
        )}
      </div>

      <div className="card">
        <h2>📚 Gợi ý tài liệu</h2>
        
        <div className="form-group">
          <label>Chủ đề muốn học</label>
          <input
            type="text"
            value={topic}
            onChange={(e) => setTopic(e.target.value)}
            placeholder="VD: Phương trình bậc 2"
          />
        </div>

        <button className="btn btn-primary btn-full" onClick={fetchMaterials} disabled={loading}>
          {loading ? '⏳ Đang tìm...' : '🔍 Tìm tài liệu phù hợp'}
        </button>

        {materials && (
          <div className="materials-result">
            <h3>📖 Tài liệu được đề xuất</h3>
            <p style={{ whiteSpace: 'pre-wrap' }}>{materials}</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
