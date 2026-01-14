# 1) Set schedule for clinician C-001 (UTC times here for simplicity)
curl -s -X PUT :8081/clinicians/C-001/schedule -H 'Content-Type: application/json' -d '{
  "slots":[
    {"start":"2025-10-21T13:00:00Z","end":"2025-10-21T13:30:00Z"},
    {"start":"2025-10-21T13:30:00Z","end":"2025-10-21T14:00:00Z"}
  ]
}'

# 2) List availability
curl -s ':8081/availability?clinicianId=C-001&from=2025-10-21T12:00:00Z&to=2025-10-21T15:00:00Z'

# 3) Book first slot (copy slotId from step 2)
curl -s -X POST :8081/appointments -H 'Content-Type: application/json' -d '{
  "clinicianId":"C-001","patientId":"P-001","slotId":"C-001#1761051600000"
}'

# 4) Start encounter (use appointmentId from step 3)
curl -s -X POST :8081/encounters/start -H 'Content-Type: application/json' -d '{
  "appointmentId":"<APPT_ID>"
}'

# 5) End encounter (use encounterId from step 4)
curl -s -X POST :8081/encounters/<ENC_ID>/end -H 'Content-Type: application/json' -d '{
  "uri":"s3://mock/rec/<ENC_ID>.mp4","durationSec":420,"format":"mp4"
}'
