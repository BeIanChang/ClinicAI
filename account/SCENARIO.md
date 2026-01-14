# 🏥 ClinicAI Account Management Service - Minimal Scenario

This document outlines a **minimal and viable scenario** focused on the **Patient Account Lifecycle** by registering and authenticating different types of users, enabling them to perform their roles within the ClinicAI system.

## 🎯 Scenario: Patient Account Lifecycle

**Goal:** To demonstrate the core functionality of the Account Management Service by successfully registering a new patient, allowing them to log in, and establishing their identity for subsequent interactions within the broader ClinicAI system.

## 🔁 Execution Steps

### Step 1: User Registration

This step creates a new account for the User(Patient, Clinician, Lab Technician) in the system.

| Detail | Value |
| :--- | :--- |
| **HTTP Request** | `POST /accounts/register` |
| **Content-Type** | `application/json` |

**Request Body Example:**

```json
{
  "id": 1,
  "name": "Weifan Wu",
  "email": "weifan@example.com",
  "password": "123456",
  "roles": ["PATIENT"]
}
````

**Expected Result:**

1.  **Response Body:** `Registered successfully`
2.  **Console Output (Mock Email):** `Sending welcome email to weifan@example.com (name: Weifan Wu)`

**Notes:**

  * The user is now an identifiable entity within the system.
  * The welcome email is currently **mocked** via a console printout, demonstrating the integration point for a real email service.

### Step 2: User Login

This step authenticates the registered clinician, allowing them to proceed with other ClinicAI services.

| Detail | Value |
| :--- | :--- |
| **HTTP Request** | `POST /accounts/login?email=weifan@example.com&password=123456` |

**Expected Result:**

The service returns the full details of the authenticated user.

```json
{
  "id": 1,
  "name": "Weifan Wu",
  "email": "weifan@example.com",
  "password": "123456",
  "roles": ["CLINICIAN"]
}
```

**Notes:**

  * Successful login authenticates the CLINICIAN session.
  * **Failure Case:** Invalid credentials should return an error, such as "Invalid email or password."


## 🌐 Integration with Other Services (Full ClinicAI Workflow)

Once the patient is successfully registered and logged in, their user identity can be used to initiate and complete various subsequent workflows across the ClinicAI microservices:

1.  **Appointment Booking & Billing:** The patient schedules an appointment and completes payment for the consultation fee.
2.  **Encounter Management:** The clinician uses the patient's ID to start an encounter, which is recorded and structured.
3.  **Lab Test Management:** The patient confirms and pays for required lab tests, and later the normalized results are uploaded against their profile.
4.  **Data Upload & AI Analysis:** The patient's structured encounter data and lab results are packaged and sent for AI processing, which returns a health risk summary.
5.  **Patient Report Retrieval:** The patient accesses their final AI-generated health report via a dedicated API using their authenticated identity.