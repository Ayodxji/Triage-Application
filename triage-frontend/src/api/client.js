import axios from 'axios';

const client = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

export const nursesApi = {
  getAll:       () => client.get('/nurses'),
  getAvailable: () => client.get('/nurses/available'),
  add:          (data) => client.post('/nurses', data),
  update:       (id, data) => client.put(`/nurses/${id}`, data),
  remove:       (id) => client.delete(`/nurses/${id}`),
};

export const patientsApi = {
  getAll:         () => client.get('/patients'),
  getById:        (id) => client.get(`/patients/${id}`),
  add:            (data) => client.post('/patients', data),
  assignSymptoms: (id, symptoms) =>
    client.post(`/patients/${id}/symptoms`, { symptoms }),
};

export const symptomsApi = {
  getAll: () => client.get('/symptoms'),
};

export const assignmentsApi = {
  run:    () => client.post('/assignments/run'),
  getAll: () => client.get('/assignments'),
};
