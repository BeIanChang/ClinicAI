import React, { useState } from 'react';

const initialState = {
  encounterId: '',
  patientId: '',
  clinicianId: '',
  originalText: '',
};

export default function SummaryForm({ token, onSubmit }) {
  const [formState, setFormState] = useState(initialState);
  const [status, setStatus] = useState({ type: 'idle', message: '' });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormState((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!token) {
      setStatus({ type: 'error', message: 'Login required to submit summaries.' });
      return;
    }
    setStatus({ type: 'pending', message: 'Submitting summary request…' });
    try {
      await onSubmit(formState);
      setStatus({ type: 'success', message: 'Summary request submitted.' });
    } catch (error) {
      setStatus({ type: 'error', message: error.message });
    }
  };

  return (
    <section className="card">
      <h2>Summarize Encounter</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Encounter ID
          <input
            name="encounterId"
            value={formState.encounterId}
            onChange={handleChange}
            placeholder="encounter_123"
            required
          />
        </label>
        <label>
          Patient ID
          <input
            name="patientId"
            value={formState.patientId}
            onChange={handleChange}
            placeholder="patient_456"
            required
          />
        </label>
        <label>
          Clinician ID
          <input
            name="clinicianId"
            value={formState.clinicianId}
            onChange={handleChange}
            placeholder="drjohnson"
            required
          />
        </label>
        <label>
          Original Text
          <textarea
            name="originalText"
            value={formState.originalText}
            onChange={handleChange}
            placeholder="Patient presents with headache and fever…"
            rows={6}
            required
          />
        </label>
        <button type="submit">Generate summary</button>
        {status.message ? (
          <p className={`status-${status.type}`}>{status.message}</p>
        ) : null}
      </form>
    </section>
  );
}
