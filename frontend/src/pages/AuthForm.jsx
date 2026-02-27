import React, { useEffect, useState } from 'react';

const initialRegisterState = {
  username: '',
  password: '',
  email: '',
  role: 'CLINICIAN',
};

const initialLoginState = {
  username: '',
  password: '',
};

const roles = ['PATIENT', 'CLINICIAN', 'RECEPTIONIST', 'ADMIN'];

export default function AuthForm({ mode, onSubmit }) {
  const [formState, setFormState] = useState(
    mode === 'register' ? initialRegisterState : initialLoginState
  );

  useEffect(() => {
    setFormState(mode === 'register' ? initialRegisterState : initialLoginState);
  }, [mode]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormState((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit(formState);
  };

  return (
    <section className="card">
      <h2>{mode === 'register' ? 'Create account' : 'Sign in'}</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Username
          <input
            name="username"
            value={formState.username}
            onChange={handleChange}
            placeholder="drjohnson"
            required
          />
        </label>
        <label>
          Password
          <input
            name="password"
            value={formState.password}
            onChange={handleChange}
            type="password"
            placeholder="securePassword123"
            required
          />
        </label>

        {mode === 'register' && (
          <>
            <label>
              Email
              <input
                name="email"
                value={formState.email}
                onChange={handleChange}
                type="email"
                placeholder="dr.johnson@clinicai.com"
                required
              />
            </label>
            <label>
              Role
              <select name="role" value={formState.role} onChange={handleChange}>
                {roles.map((role) => (
                  <option key={role} value={role}>
                    {role}
                  </option>
                ))}
              </select>
            </label>
          </>
        )}

        <button type="submit">{mode === 'register' ? 'Register' : 'Login'}</button>
      </form>
    </section>
  );
}
