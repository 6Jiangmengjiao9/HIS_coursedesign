import os
import uuid
from datetime import datetime, timedelta

import pytest
import requests

BASE_URL = os.getenv("HIS_BASE_URL", "http://localhost:8081").rstrip("/")
PASSWORD = os.getenv("HIS_DEFAULT_PASSWORD", "123456")
TIMEOUT = 15


def request(method, path, token=None, **kwargs):
    headers = dict(kwargs.pop("headers", {}))
    if token:
        headers["Authorization"] = f"Bearer {token}"
    return requests.request(method, f"{BASE_URL}{path}", headers=headers, timeout=TIMEOUT, **kwargs)


def login(username, password=PASSWORD):
    response = request("GET", "/login", params={"username": username, "password": password})
    payload = response.json()
    assert response.status_code == 200, payload
    assert payload["code"] == 0, payload
    assert payload["token"], payload
    return payload["token"], payload["data"]


def data_of(response):
    payload = response.json()
    assert response.status_code == 200, payload
    assert payload["code"] == 0, payload
    return payload.get("data")


def ok_of(response):
    payload = response.json()
    assert response.status_code == 200, payload
    assert payload["code"] == 0, payload
    return payload


@pytest.mark.parametrize(
    "username,role",
    [
        ("admin", "admin"),
        ("doctor", "doctor"),
        ("cashier", "cashier"),
        ("pharmacist", "pharmacist"),
        ("patient", "patient"),
    ],
)
def test_login_all_roles(username, role):
    token, user = login(username)
    assert token
    assert user["role"] == role
    assert "password" not in user


def test_anonymous_request_returns_401():
    response = request("GET", "/getAllDrug")
    assert response.status_code == 401
    assert response.json()["code"] == 401


def test_doctor_cannot_charge():
    token, _ = login("doctor")
    response = request("POST", "/api/charges/1", token=token)
    assert response.status_code == 403
    assert response.json()["code"] == 403


def test_patient_cannot_mark_visited():
    token, _ = login("patient")
    response = request("POST", "/api/register-records/1/visit", token=token)
    assert response.status_code == 403
    assert response.json()["code"] == 403


def test_full_outpatient_flow():
    admin_token, _ = login("admin")
    doctor_token, doctor_user = login("doctor")

    doctor_info = doctor_user.get("doctor") or {}
    doctor_id = doctor_info.get("doctorId")
    department_id = doctor_info.get("departmentId") or "D001"
    if not doctor_id:
        doctors = data_of(request("GET", "/getAllDoctor", token=admin_token))
        doctor = next(item for item in doctors if str(item.get("userId")) == str(doctor_user["id"]))
        doctor_id = doctor["doctorId"]
        department_id = doctor.get("departmentId") or department_id

    suffix = uuid.uuid4().hex[:8]
    digits = str(uuid.uuid4().int)[:8]
    patient_username = f"pytest_{suffix}"
    patient_name = f"测试患者{suffix}"
    patient_phone = f"139{digits}"
    drug_name = f"PytestDrug{suffix}"
    room_number = f"PT{suffix}"

    drug_id = None
    schedule_id = None
    register_id = None
    patient_id = None
    patient_user_id = None

    try:
        ok_of(request("POST", "/addDrug", token=admin_token, json={
            "drugName": drug_name,
            "drugType": "测试",
            "indication": "pytest",
            "applicableDepartment": department_id,
            "dosageForm": "片剂",
            "specification": "测试规格",
            "manufacturer": "pytest",
            "unitPrice": 12.34,
            "stockQuantity": 50,
            "status": 1,
        }))
        drugs = data_of(request("GET", "/getAllDrug", token=admin_token))
        drug = next(item for item in drugs if item["drugName"] == drug_name)
        drug_id = drug["drugId"]
        initial_stock = drug["stockQuantity"]

        schedule_date = datetime.now() + timedelta(days=3)
        schedule_date = schedule_date.replace(hour=12, minute=0, second=0, microsecond=0)
        ok_of(request("POST", "/addSchedule", token=admin_token, json={
            "doctorId": doctor_id,
            "scheduleDate": int(schedule_date.timestamp() * 1000),
            "shiftType": "上午",
            "startTime": "08:00",
            "endTime": "12:00",
            "departmentId": department_id,
            "roomNumber": room_number,
            "maxPatients": 5,
            "remainingSlots": 5,
            "status": 1,
        }))
        schedules = data_of(request("GET", "/getAllSchedule", token=admin_token))
        schedule = next(item for item in schedules if item.get("roomNumber") == room_number)
        schedule_id = schedule["scheduleId"]
        assert schedule["remainingSlots"] == 5

        ok_of(request("GET", "/register", params={
            "username": patient_username,
            "password": PASSWORD,
            "email": f"{patient_username}@pytest.local",
            "phone": patient_phone,
            "role": "patient",
            "relname": patient_name,
            "gender": 1,
        }))
        patient_token, patient_user = login(patient_username)
        patient_user_id = patient_user["id"]
        patients = data_of(request("GET", "/getAllPatient", token=patient_token))
        patient = next(
            item for item in patients
            if item["name"] == patient_name and item["contact"] == patient_phone
        )
        patient_id = patient["patientId"]

        created = data_of(request("POST", "/api/register-records", token=patient_token, json={
            "patientId": patient_id,
            "scheduleId": schedule_id,
            "departmentId": department_id,
            "doctorId": doctor_id,
            "patientName": patient_name,
            "patientAge": 30,
            "patientPhone": patient_phone,
            "reservationStatus": 1,
            "visitStatus": 0,
        }))
        register_id = created["id"]
        assert created["registerStatus"] == 0
        assert created["visitStatus"] == 0

        schedules = data_of(request("GET", "/getAllSchedule", token=admin_token))
        schedule = next(item for item in schedules if item["scheduleId"] == schedule_id)
        assert schedule["remainingSlots"] == 4

        visited = data_of(request(
            "POST", f"/api/register-records/{register_id}/visit", token=doctor_token))
        assert visited["visitStatus"] == 1

        prescription = data_of(request("POST", "/addPrescription", token=doctor_token, json={
            "registerId": register_id,
            "patientId": patient_id,
            "doctorId": doctor_id,
            "diagnosis": "pytest 测试诊断",
            "totalFee": 0.01,
            "items": [{"medicineId": drug_id, "quantity": 2}],
        }))
        prescription_id = prescription["prescriptionId"]
        assert prescription["status"] == 0
        assert prescription["totalFee"] == pytest.approx(24.68, abs=0.001)

        cashier_token, _ = login("cashier")
        charge = data_of(request("POST", f"/api/charges/{prescription_id}", token=cashier_token))
        assert charge["amount"] == pytest.approx(24.68, abs=0.001)

        drugs = data_of(request("GET", "/getAllDrug", token=admin_token))
        drug = next(item for item in drugs if item["drugId"] == drug_id)
        assert drug["stockQuantity"] == initial_stock - 2

        pharmacist_token, _ = login("pharmacist")
        dispensed = data_of(request(
            "POST", f"/api/dispenses/{prescription_id}", token=pharmacist_token))
        assert dispensed["status"] == 2

        records = data_of(request("GET", "/api/register-records/my", token=patient_token))
        record = next(item for item in records if item["id"] == register_id)
        assert record["registerStatus"] == 2

        logs = data_of(request(
            "GET", "/api/audit-logs",
            token=pharmacist_token,
            params={"pageNum": 1, "pageSize": 200},
        ))

        def has_audit(action, keyword):
            return any(
                log["action"] == action and keyword in (log.get("detail") or "")
                for log in logs
            )

        assert has_audit("REGISTER", f"挂号单ID={register_id}")
        assert has_audit("VISIT", f"挂号单ID={register_id}")
        assert has_audit("PRESCRIBE", f"挂号单ID={register_id}")
        assert has_audit("CHARGE", f"处方ID={prescription_id}")
        assert has_audit("DISPENSE", f"处方ID={prescription_id}")
    finally:
        if register_id is not None:
            request("GET", "/deleteReservation", token=admin_token, params={"id": register_id})
        if schedule_id is not None:
            request("GET", "/deleteSchedule", token=admin_token, params={"scheduleId": schedule_id})
        if drug_id is not None:
            request("GET", "/deleteDrug", token=admin_token, params={"drugId": drug_id})
        if patient_id is not None:
            request("GET", "/deletePatient", token=admin_token, params={"patientId": patient_id})
        if patient_user_id is not None:
            request("GET", "/deleteUser", token=admin_token, params={"id": patient_user_id})
