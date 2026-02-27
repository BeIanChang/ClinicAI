import React, { useMemo, useState } from 'react';
import AuthForm from './pages/AuthForm.jsx';
import SummaryForm from './pages/SummaryForm.jsx';
import { login, register, validateToken } from './api/auth.js';
import { generateSummary } from './api/summaries.js';

const defaultMessage = 'Sign in or register to get a JWT token.';

export default function App() {
  const [mode, setMode] = useState('login');
  const [token, setToken] = useState('');
  const [status, setStatus] = useState({ type: 'info', message: defaultMessage });

  const isAuthenticated = useMemo(() => Boolean(token), [token]);

  const handleSubmit = async (formData) => {
    setStatus({ type: 'pending', message: 'Contacting auth service…' });

    try {
      if (mode === 'register') {
        const response = await register(formData);
        const maybeToken = response?.token ?? response?.accessToken ?? '';
        setToken(maybeToken);
        setStatus({
          type: 'success',
          message: maybeToken
            ? 'Registration successful. Token stored.'
            : 'Registration successful. No token returned.',
        });
        return;
      }

      const response = await login({
        username: formData.username,
        password: formData.password,
      });
      const maybeToken = response?.token ?? response?.accessToken ?? '';
      setToken(maybeToken);
      setStatus({
        type: 'success',
        message: maybeToken ? 'Login successful. Token stored.' : 'Login successful.',
      });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  };

  const handleValidate = async () => {
    if (!token) {
      setStatus({ type: 'error', message: 'No token available to validate.' });
      return;
    }

    setStatus({ type: 'pending', message: 'Validating token…' });

    try {
      await validateToken(token);
      setStatus({ type: 'success', message: 'Token is valid.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  };

  const handleSummarize = async (payload) => {
    if (!token) {
      throw new Error('Login required to request summarization.');
    }
    const response = await generateSummary(payload, token);
    return response;
  };

  return (
    <div className="app">
      <header>
        <div>
          <p className="eyebrow">ClinicAI</p>
          <h1>Authentication Console</h1>
          <p className="subtitle">
            Use the auth service at <strong>{import.meta.env.VITE_AUTH_BASE_URL}</strong>
          </p>
        </div>
        <div className="mode-toggle">
          <button
            type="button"
            className={mode === 'login' ? 'active' : ''}
            onClick={() => setMode('login')}
          >
            Login
          </button>
          <button
            type="button"
            className={mode === 'register' ? 'active' : ''}
            onClick={() => setMode('register')}
          >
            Register
          </button>
        </div>
      </header>

      <main>
        <AuthForm mode={mode} onSubmit={handleSubmit} />

        <SummaryForm token={token} onSubmit={handleSummarize} />

        <section className="status">
          <h2>Status</h2>
          <p className={`status-${status.type}`}>{status.message}</p>
          <div className="status-actions">
            <button type="button" onClick={() => setToken('')}>
              Clear token
            </button>
            <button type="button" onClick={handleValidate}>
              Validate token
            </button>
          </div>
        </section>

        <section className="token">
          <h2>Current Token</h2>
          <textarea
            readOnly
            value={token || 'Token will appear here after login.'}
            rows={6}
          />
        </section>
      </main>
    </div>
  );
}
