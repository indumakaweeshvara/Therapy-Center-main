package org.example.bo.custom.impl;

import org.example.bo.custom.PatientBO;
import org.example.config.FactoryConfiguration;
import org.example.dao.DAOFactory;
import org.example.dao.DAOTypes;
import org.example.dao.custom.PatientDAO;
import org.example.dao.custom.PaymentDAO;
import org.example.dao.custom.RegistrationDAO;
import org.example.dto.PatientDto;
import org.example.entity.*;
import org.example.view.tdm.PatientTm;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientBOImpl implements PatientBO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    PatientDAO patientDAO  = DAOFactory.getInstance().getDAO(DAOTypes.PATIENT);
    RegistrationDAO registrationDAO  = DAOFactory.getInstance().getDAO(DAOTypes.REGISTRATION);
    PaymentDAO paymentDAO  = DAOFactory.getInstance().getDAO(DAOTypes.PAYMENT);

    @Override
    public String getNextPatientId() throws SQLException {
        Session session = factoryConfiguration.getInstance().getSession();
        try {
            int lastId = patientDAO.getLastId(session);
            return String.format("P%03d", (lastId + 1));
        } finally {
            session.close();
        }
    }

    @Override
    public boolean savePatient(PatientDto patientDTO) throws SQLException {
        Session session = factoryConfiguration.getInstance().getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Create Patient entity
            Patients patient = new Patients();
            patient.setName(patientDTO.getName());
            patient.setGender(patientDTO.getGender());
            patient.setAge(patientDTO.getAge());
            patient.setAddress(patientDTO.getAddress());
            patient.setPhone(patientDTO.getPhone());
            patient.setEmail(patientDTO.getEmail());
            patient.setRemainingSessions(patientDTO.getRemainingSessions());

            // Set user relationship
            Users user = new Users();
            user.setId(patientDTO.getUserId());
            patient.setUser(user);

            // Save patient first
            boolean patientSaved = patientDAO.save(patient, session);
            if (!patientSaved) {
                transaction.rollback();
                return false;
            }

            // Create Registration entity
            Registration registration = new Registration();
            RegistrationId registrationId = new RegistrationId(patientDTO.getProgramId(), patient.getId());
            registration.setId(registrationId);

            // Set patient relationship
            registration.setPatient(patient);

            // Set program relationship
            TherapyProgram therapyProgram = session.get(TherapyProgram.class, patientDTO.getProgramId());
            registration.setProgram(therapyProgram);

            registration.setSessionCount(patientDTO.getSessionCount());
            registration.setDate(patientDTO.getRegistrationDate());
            registration.setTime(patientDTO.getRegistrationTime());

            // Save registration
            boolean registrationSaved = registrationDAO.save(registration, session);
            if (!registrationSaved) {
                transaction.rollback();
                return false;
            }

            // Create Payment entity
            Payment payment = new Payment();
            payment.setPaymentType(patientDTO.getPaymentType());
            payment.setAmount(patientDTO.getAmount());
            payment.setBalancePayment(patientDTO.getBalancePayment());
            payment.setDate(patientDTO.getPaymentDate());
            payment.setTime(patientDTO.getPaymentTime());
            payment.setPatient(patient);
            payment.setProgram(therapyProgram);

            // Save payment
            boolean paymentSaved = paymentDAO.save(payment, session);
            if (!paymentSaved) {
                transaction.rollback();
                return false;
            }

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public List<PatientTm> getAllPatients() throws SQLException {
        Session session = factoryConfiguration.getInstance().getSession();
        try {
            List<Patients> patients = patientDAO.getAll(session);
            List<PatientTm> patientTMS = new ArrayList<>();

            for (Patients patient : patients) {
                patientTMS.add(new PatientTm(
                        patient.getId(),
                        patient.getName(),
                        patient.getGender(),
                        patient.getAge(),
                        patient.getPhone(),
                        patient.getEmail(),
                        patient.getRemainingSessions()
                ));
            }

            return patientTMS;
        } finally {
            session.close();
        }
    }

    @Override
    public PatientDto getPatientById(int id) throws SQLException {
        Session session = factoryConfiguration.getInstance().getSession();
        try {
            Patients patient = patientDAO.get(id, session);
            if (patient == null) {
                return null;
            }

            PatientDto patientDTO = new PatientDto();
            patientDTO.setId(patient.getId());
            patientDTO.setName(patient.getName());
            patientDTO.setGender(patient.getGender());
            patientDTO.setAge(patient.getAge());
            patientDTO.setAddress(patient.getAddress());
            patientDTO.setPhone(patient.getPhone());
            patientDTO.setEmail(patient.getEmail());
            patientDTO.setRemainingSessions(patient.getRemainingSessions());

            if (patient.getUser() != null) {
                patientDTO.setUserId(patient.getUser().getId());
            }

            Payment payment = paymentDAO.get(patient.getId(), session);
            if (payment != null) {
                patientDTO.setPaymentType(payment.getPaymentType());
                patientDTO.setAmount(payment.getAmount());
                patientDTO.setBalancePayment(payment.getBalancePayment());
                patientDTO.setPaymentDate(payment.getDate());
                patientDTO.setPaymentTime(payment.getTime());
            }

            Registration registration = registrationDAO.get(patient.getId(), session);
            if (registration != null) {
                patientDTO.setProgramId(registration.getProgram().getProgramId());
                patientDTO.setSessionCount(registration.getSessionCount());
                patientDTO.setRegistrationDate(registration.getDate());
                patientDTO.setRegistrationTime(registration.getTime());
            }

            return patientDTO;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean deletePatient(int id) throws SQLException {
        Session session = factoryConfiguration.getInstance().getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Patients patient = session.get(Patients.class, id);
            if (patient == null) {
                return false;
            }

            boolean delete = patientDAO.delete(patient, session);
            if (!delete) {
                transaction.rollback();
                return false;
            }


            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updatePatient(PatientDto patientDTO) throws SQLException {
        Session session = factoryConfiguration.getInstance().getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Load the existing patient
            Patients patient = session.get(Patients.class, patientDTO.getId());
            if (patient == null) {
                transaction.rollback();
                return false;
            }

            // Update patient properties
            patient.setName(patientDTO.getName());
            patient.setGender(patientDTO.getGender());
            patient.setAge(patientDTO.getAge());
            patient.setAddress(patientDTO.getAddress());
            patient.setPhone(patientDTO.getPhone());
            patient.setEmail(patientDTO.getEmail());
            patient.setRemainingSessions(patientDTO.getRemainingSessions());

            // Load the existing user
            Users user = session.get(Users.class, patientDTO.getUserId());
            patient.setUser(user);

            // Update patient
            boolean patientUpdated = patientDAO.update(patient, session);
            if (!patientUpdated) {
                transaction.rollback();
                return false;
            }

            // Find existing registration
            String hql = "FROM Registration r WHERE r.patient.id = :patientId";
            Query<Registration> query = session.createQuery(hql, Registration.class);
            query.setParameter("patientId", patient.getId());
            List<Registration> existingRegistrations = query.getResultList();

            // Load therapy program
            TherapyProgram therapyProgram = session.get(TherapyProgram.class, patientDTO.getProgramId());

            if (!existingRegistrations.isEmpty()) {
                Registration existingRegistration = existingRegistrations.get(0);
                String existingProgramId = existingRegistration.getProgram().getProgramId();

                // If program has changed, we need to remove the old registration and create a new one
                if (!existingProgramId.equals(patientDTO.getProgramId())) {
                    // Remove existing registration first
                    registrationDAO.delete(existingRegistration, session);

                    // Create new registration with new ID
                    Registration newRegistration = new Registration();
                    RegistrationId registrationId = new RegistrationId(patientDTO.getProgramId(), patient.getId());
                    newRegistration.setId(registrationId);
                    newRegistration.setPatient(patient);
                    newRegistration.setProgram(therapyProgram);
                    newRegistration.setSessionCount(patientDTO.getSessionCount());
                    newRegistration.setDate(patientDTO.getRegistrationDate());
                    newRegistration.setTime(patientDTO.getRegistrationTime());

                    boolean registrationSaved = registrationDAO.save(newRegistration, session);
                    if (!registrationSaved) {
                        transaction.rollback();
                        return false;
                    }
                } else {
                    // Only update the session count and dates but not the ID
                    existingRegistration.setSessionCount(patientDTO.getSessionCount());
                    existingRegistration.setDate(patientDTO.getRegistrationDate());
                    existingRegistration.setTime(patientDTO.getRegistrationTime());

                    boolean registrationUpdated = registrationDAO.update(existingRegistration, session);
                    if (!registrationUpdated) {
                        transaction.rollback();
                        return false;
                    }
                }
            } else {
                // Create new registration if none exists (shouldn't normally happen in update)
                Registration newRegistration = new Registration();
                RegistrationId registrationId = new RegistrationId(patientDTO.getProgramId(), patient.getId());
                newRegistration.setId(registrationId);
                newRegistration.setPatient(patient);
                newRegistration.setProgram(therapyProgram);
                newRegistration.setSessionCount(patientDTO.getSessionCount());
                newRegistration.setDate(patientDTO.getRegistrationDate());
                newRegistration.setTime(patientDTO.getRegistrationTime());

                boolean registrationSaved = registrationDAO.save(newRegistration, session);
                if (!registrationSaved) {
                    transaction.rollback();
                    return false;
                }
            }

            // Find existing payment
            String paymentHql = "FROM Payment p WHERE p.patient.id = :patientId";
            Query<Payment> paymentQuery = session.createQuery(paymentHql, Payment.class);
            paymentQuery.setParameter("patientId", patient.getId());
            List<Payment> existingPayments = paymentQuery.getResultList();

            if (!existingPayments.isEmpty()) {
                // Update existing payment
                Payment payment = existingPayments.get(0);

                payment.setPaymentType(patientDTO.getPaymentType());
                payment.setAmount(patientDTO.getAmount());
                payment.setBalancePayment(patientDTO.getBalancePayment());
                payment.setDate(patientDTO.getPaymentDate());
                payment.setTime(patientDTO.getPaymentTime());
                payment.setProgram(therapyProgram);

                boolean paymentUpdated = paymentDAO.update(payment, session);
                if (!paymentUpdated) {
                    transaction.rollback();
                    return false;
                }
            } else {
                // Create new payment if none exists (shouldn't normally happen in update)
                Payment newPayment = new Payment();
                newPayment.setPaymentType(patientDTO.getPaymentType());
                newPayment.setAmount(patientDTO.getAmount());
                newPayment.setBalancePayment(patientDTO.getBalancePayment());
                newPayment.setDate(patientDTO.getPaymentDate());
                newPayment.setTime(patientDTO.getPaymentTime());
                newPayment.setPatient(patient);
                newPayment.setProgram(therapyProgram);

                boolean paymentSaved = paymentDAO.save(newPayment, session);
                if (!paymentSaved) {
                    transaction.rollback();
                    return false;
                }
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public int getRemainingSessions(int patientId) {
       return patientDAO.getRemainingSessions(patientId);
    }
}
