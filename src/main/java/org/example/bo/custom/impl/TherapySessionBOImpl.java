package org.example.bo.custom.impl;

import org.example.bo.custom.TherapySessionBO;
import org.example.config.FactoryConfiguration;
import org.example.dao.DAOFactory;
import org.example.dao.DAOTypes;
import org.example.dao.custom.PatientDAO;
import org.example.dao.custom.PaymentDAO;
import org.example.dao.custom.TherapistDAO;
import org.example.dao.custom.TherapySessionDAO;
import org.example.dto.TherapySessionDto;
import org.example.entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TherapySessionBOImpl implements TherapySessionBO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    TherapySessionDAO therapySessionDAO = DAOFactory.getInstance().getDAO(DAOTypes.THERAPY_SESSION);
    PatientDAO patientDAO = DAOFactory.getInstance().getDAO(DAOTypes.PATIENT);
    TherapistDAO therapistDAO = DAOFactory.getInstance().getDAO(DAOTypes.THERAPIST);
    PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOTypes.PAYMENT);


    @Override
    public boolean scheduleSession(TherapySessionDto therapySessionDTO, double paymentAmount) {
        Session session = factoryConfiguration.getInstance().getSession();
        Transaction transaction=null;

        try{
            transaction = session.beginTransaction();

            // Get the patient and therapist
            Patients patient = patientDAO.get(therapySessionDTO.getPatientId(), session);
            Therapist therapist = therapistDAO.get(therapySessionDTO.getTherapistId(), session);

            if (patient == null || therapist == null) {
                transaction.rollback();
                return false;
            }

            // Check if patient has remaining sessions
            if (patient.getRemainingSessions() <= 0) {
                transaction.rollback();
                return false;
            }

            // Save the therapy session
            TherapySessionId id = new TherapySessionId(therapist.getId(), patient.getId());

            TherapySession therapySession=new TherapySession();
            therapySession.setTherapist(therapist);
            therapySession.setPatient(patient);
            therapySession.setTherapySessionId(id);
            therapySession.setDate(therapySessionDTO.getDate());
            therapySession.setTime(therapySessionDTO.getTime());
            therapySession.setStatus(therapySessionDTO.getStatus());
            therapySession.setSessionNote(therapySessionDTO.getSessionNote());

            boolean sessionSaved = therapySessionDAO.save(therapySession, session);

            if (!sessionSaved) {
                transaction.rollback();
                return false;
            }

            // Update the patient's remaining sessions based on session status
            String status = therapySessionDTO.getStatus();

            if (status.equals("Completed") || status.equals("No-show")) {
                // Reduce remaining sessions for both Completed and No-show
                patient.setRemainingSessions(patient.getRemainingSessions() - 1);
                boolean patientUpdated = patientDAO.update(patient, session);
                if (!patientUpdated) {
                    transaction.rollback();
                    return false;
                }
            }

            // Save the payment
            Payment pAyDetail = paymentDAO.get(patient.getId(), session);
            // Create Payment entity
            Payment payment = new Payment();
            payment.setPaymentType(pAyDetail.getPaymentType());
            payment.setAmount(BigDecimal.valueOf(paymentAmount));
            payment.setBalancePayment(pAyDetail.getBalancePayment().subtract(BigDecimal.valueOf(paymentAmount)));
            payment.setDate(therapySessionDTO.getDate());
            payment.setTime(therapySessionDTO.getTime());
            payment.setPatient(patient);
            payment.setProgram(pAyDetail.getProgram());

            // Save payment
            boolean paymentSaved = paymentDAO.save(payment, session);
            if (!paymentSaved) {
                transaction.rollback();
                return false;
            }

            transaction.commit();
            return true;

        }catch (Exception e) {
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
    public boolean isSessionConflict(int patientId, int therapistId, String date, String time) {
        return therapySessionDAO.isSessionConflict(patientId, therapistId, date, time);

    }

    @Override
    public List<TherapySessionDto> getAllSessions() {
        try {
            List<TherapySession> sessions = therapySessionDAO.getAll();
            return convertToDTO(sessions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean ReScheduleSession(TherapySessionDto sessionDTO, double paymentAmount, TherapySessionId id) {
        Session session = factoryConfiguration.getInstance().getSession();
        Transaction transaction=null;

        try{
            transaction = session.beginTransaction();

            // Get the therapy session
            TherapySession therapySession = session.get(TherapySession.class, id);
            if (therapySession == null) {
                transaction.rollback();
                return false;
            }
            // Update the patient's remaining sessions based on session status
            String newStatus = sessionDTO.getStatus();
            String currentStatus = therapySession.getStatus();

            if (!currentStatus.equals(newStatus)) {

                boolean isCurrentCompletedOrNoShow = currentStatus.equals("Completed") || currentStatus.equals("No-show");
                boolean isNewCompletedOrNoShow = newStatus.equals("Completed") || newStatus.equals("No-show");
                boolean isCurrentScheduledOrCancelled = currentStatus.equals("Scheduled") || currentStatus.equals("Cancelled");
                boolean isNewScheduledOrCancelled = newStatus.equals("Scheduled") || newStatus.equals("Cancelled");

                // Do nothing if just switching between Completed <-> No-show
                if (isCurrentCompletedOrNoShow && isNewCompletedOrNoShow) {
                    // No session count changes
                }
                // Add +1 if changing from Completed/No-show → Scheduled/Cancelled
                else if (isCurrentCompletedOrNoShow && isNewScheduledOrCancelled) {
                    Patients patient = therapySession.getPatient();
                    patient.setRemainingSessions(patient.getRemainingSessions() + 1);

                    boolean patientUpdated = patientDAO.update(patient, session);
                    if (!patientUpdated) {
                        transaction.rollback();
                        return false;
                    }
                }
                // Subtract -1 if changing from Scheduled/Cancelled → Completed/No-show
                else if (isCurrentScheduledOrCancelled && isNewCompletedOrNoShow) {
                    Patients patient = therapySession.getPatient();
                    patient.setRemainingSessions(patient.getRemainingSessions() - 1);

                    boolean patientUpdated = patientDAO.update(patient, session);
                    if (!patientUpdated) {
                        transaction.rollback();
                        return false;
                    }
                }
            }

            // Update the therapy session
            Therapist therapist = therapistDAO.get(sessionDTO.getTherapistId(), session);
            Patients patient = patientDAO.get(sessionDTO.getPatientId(), session);
            therapySession.setTherapist(therapist);
            therapySession.setPatient(patient);
            therapySession.setDate(sessionDTO.getDate());
            therapySession.setTime(sessionDTO.getTime());
            therapySession.setStatus(sessionDTO.getStatus());
            therapySession.setSessionNote(sessionDTO.getSessionNote());
            boolean sessionUpdated = therapySessionDAO.updateSession(therapySession, session);
            if (!sessionUpdated) {
                transaction.rollback();
                return false;
            }

            //Update the payment
            Payment payment = paymentDAO.get(patient.getId(), session);
            if (payment == null) {
                transaction.rollback();
                return false;
            }

// First, restore the old amount back to the balance
            payment.setBalancePayment(payment.getBalancePayment().add(payment.getAmount()));

// Then update the new payment amount
            payment.setAmount(BigDecimal.valueOf(paymentAmount));

// Now subtract the new payment amount from balance
            payment.setBalancePayment(payment.getBalancePayment().subtract(BigDecimal.valueOf(paymentAmount)));

// Update the rest of the details
            payment.setDate(sessionDTO.getDate());
            payment.setTime(sessionDTO.getTime());
            payment.setPatient(patient);

// Update the payment record
            boolean paymentUpdated = paymentDAO.update(payment, session);
            if (!paymentUpdated) {
                transaction.rollback();
                return false;
            }

            transaction.commit();
            return true;


        }
        catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
        finally {
            session.close();
        }
    }

    @Override
    public boolean isSessionConflictUpdate(int patientId, int therapistId, String date, String time, TherapySessionId id) {
        return therapySessionDAO.isSessionConflictOnUpdate(patientId, therapistId, date, time, id);
    }


    private List<TherapySessionDto> convertToDTO(List<TherapySession> sessions) {
        List<TherapySessionDto> dtos = new ArrayList<>();

        if (sessions == null) {
            return dtos;
        }

        for (TherapySession session : sessions) {
            TherapySessionDto dto = new TherapySessionDto(
                    session.getPatient().getId(),
                    session.getTherapist().getId(),
                    session.getDate(),
                    session.getTime(),
                    session.getStatus(),
                    session.getSessionNote(),
                    session.getPatient().getName(),
                    session.getTherapist().getName()
            );
            dtos.add(dto);
        }

        return dtos;
    }
}
