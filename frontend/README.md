# ClinicAI Frontend

React + Vite UI for the ClinicAI services.

## Setup

```bash
npm install
```

## Configure API

Create a `.env` file from the example:

```bash
cp .env.example .env
```

Update `VITE_AUTH_BASE_URL` (auth service) and `VITE_SUMMARY_BASE_URL` (summarization service).

## Run

```bash
npm run dev
```

Open `http://localhost:5173` and use the login/register form to obtain a JWT.
