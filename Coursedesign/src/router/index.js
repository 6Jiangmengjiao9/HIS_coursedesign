import { createRouter, createWebHistory } from 'vue-router'
import Patient from '../components/AdminPatient.vue'
import Doctor from '../components/AdminDoctor.vue'
import Department from '@/components/AdminDepartment.vue';
import Reservation from '@/components/AdminReservation.vue';
import Statistics from '@/components/AdminStatistics.vue';
import Login from '@/components/Login.vue';
import Register from '@/components/Register.vue';
import Admin from '@/components/AdminPanel.vue';
import User from '@/components/User.vue';
import UserMain from '@/components/UserMain.vue';
import UserOverview from '@/components/UserOverview.vue';
import UserPatient from '@/components/UserPatient.vue';
import UserMedicalRecord from '@/components/UserMedicalRecord.vue';
import UserDiagnosis from '@/components/UserDiagnosis.vue';
import UserPrescription from '@/components/UserPrescription.vue';
import AdminDrug from '@/components/AdminDrug.vue';
import UserSchedule from '@/components/UserSchedule.vue';
import AdminSchedule from '@/components/AdminSchedule.vue';
import PatientUse from '@/components/PatientUse.vue';
import PatientAIChat from '@/components/PatientAIChat.vue';
import PatientReservation from '@/components/PatientReservation.vue';
import PatientCase from '@/components/PatientCase.vue';
import PatientPrescription from '@/components/PatientPrescription.vue';
import PatientHistory from '@/components/PatientHistory.vue';
import PatientSchedule from '@/components/PatientSchedule.vue';
import PatientExpense from '@/components/PatientExpense.vue';
import PatientProfile from '@/components/PatientProfile.vue';
import UserReservations from '@/components/UserReservations.vue';
import Cashier from '@/components/Cashier.vue';
import Pharmacist from '@/components/Pharmacist.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/admin',
    component: Admin,
    meta: { roles: ['admin'] },
    children: [
      { path: '/admin/doctor', component: Doctor },
      { path: '/admin/patient', component: Patient },
      { path: '/admin/department', component: Department },
      { path: '/admin/reservation', component: Reservation },
      { path: '/admin/statistics', component: Statistics },
      { path: '/admin/drug', component: AdminDrug },
      { path: '/admin/schedule', component: AdminSchedule }
    ]
  },
  {
    path: '/user',
    component: User,
    redirect: '/user/main',
    meta: { roles: ['doctor'] },
    children: [
      { path: 'main', name: 'UserMain', component: UserMain },
      { path: 'overview', name: 'UserOverview', component: UserOverview },
      { path: 'patient', name: 'UserPatient', component: UserPatient },
      { path: 'medical-record', name: 'UserMedicalRecord', component: UserMedicalRecord },
      { path: 'diagnosis', name: 'UserDiagnosis', component: UserDiagnosis },
      { path: 'prescription', name: 'UserPrescription', component: UserPrescription },
      { path: 'schedule', name: 'UserSchedule', component: UserSchedule },
      { path: 'reservations', name: 'UserReservations', component: UserReservations }
    ]
  },
  {
    path: '/patient',
    component: PatientUse,
    redirect: '/patient/reservation',
    meta: { roles: ['patient'] },
    children: [
      { path: 'reservation', name: 'PatientReservation', component: PatientReservation },
      { path: 'case', name: 'PatientCase', component: PatientCase },
      { path: 'prescription', name: 'PatientPrescription', component: PatientPrescription },
      { path: 'history', name: 'PatientHistory', component: PatientHistory },
      { path: 'schedule', name: 'PatientSchedule', component: PatientSchedule },
      { path: 'expense', name: 'PatientExpense', component: PatientExpense },
      { path: 'profile', name: 'PatientProfile', component: PatientProfile },
      { path: 'ai-chat', name: 'PatientAIChat', component: PatientAIChat }
    ]
  },
  {
    path: '/cashier',
    component: Cashier,
    meta: { roles: ['cashier'] }
  },
  {
    path: '/pharmacist',
    component: Pharmacist,
    meta: { roles: ['pharmacist'] }
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

const homeByRole = {
  admin: '/admin/patient',
  doctor: '/user/overview',
  patient: '/patient/reservation',
  cashier: '/cashier',
  pharmacist: '/pharmacist'
};

router.beforeEach((to) => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (to.path === '/login' || to.path === '/register') {
    return token && role ? homeByRole[role] || true : true;
  }
  if (!token) {
    return '/login';
  }
  const allowedRoles = to.matched.flatMap((record) => record.meta?.roles || []);
  if (allowedRoles.length && !allowedRoles.includes(role)) {
    return homeByRole[role] || '/login';
  }
  return true;
});

export default router
