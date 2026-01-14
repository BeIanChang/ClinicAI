# 💉 ClinicAI Lab Test Management Service - SCENARIO.md

This document outlines a **minimal and viable scenario** demonstrating the **full lifecycle of a single lab test** and the resulting asynchronous communication via RabbitMQ.

## 🎯 Scenario: Full Lab Test Workflow

**Goal:** To simulate the entire process from test scheduling to completion, verifying that status transitions are correct and that necessary asynchronous events (email notifications, result uploads) are triggered.


## 🔁 Execution Steps

### Step 1: Clinician Schedules Test

A new test request is created in the system. The service generates a unique ID, here assumed to be `LT-100` for tracking.

| Detail | Value |
| :--- | :--- |
| **HTTP Request** | `POST /labtests/create-schedule` |
| **Initial Status** | **`SCHEDULED`** |

**Request Body:**

```json
{
  "patientName": "Weifan Wu",
  "testType": "Blood Panel"
}
```

**Expected Response (Example):**

```json
{
  "id": "LT-100",
  "patientName": "Weifan Wu",
  "testType": "Blood Panel",
  "status": "SCHEDULED"
}
```


### Step 2: Patient Confirms Test (Triggers Email Notification)

The patient confirms payment or consent, moving the test to the ready state. This action triggers a RabbitMQ event.

| Detail | Value |
| :--- | :--- |
| **HTTP Request** | `PUT /labtests/LT-100/confirm` |
| **Status Transition** | `SCHEDULED` → **`CONFIRMED`** |

**Expected Asynchronous Actions (Console Output):**

1.  **Event Publishing (Core):**
    `[RabbitMQ] LabTestConfirmedEvent sent → LT-100`
2.  **Event Listening (Adapter):**
    `[RabbitMQ Listener] Received LabTestConfirmedEvent → LT-100`
3.  **External Service Call (Mock):**
    `[EmailService] Sent email for LabTest → LT-100 | message: Lab test confirmed for patient: Weifan Wu`


### Step 3: Lab Technician Starts Test

The lab staff begins the analysis process.

| Detail | Value |
| :--- | :--- |
| **HTTP Request** | `PUT /labtests/LT-100/start` |
| **Status Transition** | `CONFIRMED` → **`IN_PROGRESS`** |

**Expected Result:** The test status is updated to **`IN_PROGRESS`**. No asynchronous events are triggered in this step.


### Step 4: Lab Technician Completes Test (Triggers Upload and Final Notification)

The test is complete, and results are available. This final state change triggers two asynchronous actions: result upload and notification.

| Detail | Value |
| :--- | :--- |
| **HTTP Request** | `PUT /labtests/LT-100/complete` |
| **Status Transition** | `IN_PROGRESS` → **`COMPLETED`** |

**Expected Asynchronous Actions (Console Output):**

1.  **Event Publishing (Core):**
    `[RabbitMQ] LabTestCompletedEvent sent → LT-100`
2.  **Event Listening (Adapter):**
    `[RabbitMQ Listener] Received LabTestCompletedEvent → LT-100`
3.  **External Service Call 1 (Mock Upload):**
    `[UploadService] Uploaded lab test result → LT-100`
4.  **External Service Call 2 (Mock Email):**
    `[EmailService] Sent email for LabTest → LT-100 | message: Lab test LT-100 has been completed and uploaded.`