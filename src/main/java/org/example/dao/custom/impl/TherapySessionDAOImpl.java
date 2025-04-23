package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.TherapySessionDAO;
import org.example.entity.TherapySession;
import org.example.entity.TherapySessionId;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TherapySessionDAOImpl implements TherapySessionDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    @Override
    public List<TherapySession> getAll() throws Exception {
        Session session = factoryConfiguration.getSession();
        try {
            Query<TherapySession> query = session.createQuery("FROM TherapySession ", TherapySession.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean save(TherapySession therapySession) throws SQLException {
        return false;
    }

    @Override
    public boolean update(TherapySession therapySession) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteByPK(TherapySessionId pk) throws SQLException {
        return false;
    }

    @Override
    public boolean isSessionConflict(int patientId, int therapistId, String date, String time) {
        Session session2 = FactoryConfiguration.getInstance().getSession();
        try {
            Query<TherapySession> query = session2.createQuery(
                    "FROM TherapySession ts WHERE " +
                            "(ts.patient.id = :patientId OR ts.therapist.id = :therapistId) " +
                            "AND ts.date = :date AND ts.time = :time", TherapySession.class);

            query.setParameter("patientId", patientId);
            query.setParameter("therapistId", therapistId);
            query.setParameter("date", date);
            query.setParameter("time", time);

            return !query.list().isEmpty(); // true if conflict exists
        } finally {
            session2.close();
        }
    }

    @Override
    public boolean isSessionConflictOnUpdate(int patientId, int therapistId, String date, String time, TherapySessionId currentSessionId) {
        Session session2 = FactoryConfiguration.getInstance().getSession();
        try {
            Query<TherapySession> query = session2.createQuery(
                    "FROM TherapySession ts WHERE " +
                            "(ts.patient.id = :patientId OR ts.therapist.id = :therapistId) " +
                            "AND ts.date = :date AND ts.time = :time " +
                            "AND ts.id != :currentSessionId", TherapySession.class);

            query.setParameter("patientId", patientId);
            query.setParameter("therapistId", therapistId);
            query.setParameter("date", date);
            query.setParameter("time", time);
            query.setParameter("currentSessionId", currentSessionId);

            return !query.list().isEmpty(); // true = conflict exists
        } finally {
            session2.close();
        }
    }


    @Override
    public boolean save(TherapySession therapySession, Session session) throws SQLException {
        try {
            session.persist(therapySession);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSession(TherapySession therapySession, Session session) {
        try {
            session.merge(therapySession);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
