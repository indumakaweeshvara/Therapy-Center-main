package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.RegistrationDAO;
import org.example.entity.Registration;
import org.example.entity.RegistrationId;
import org.example.entity.TherapyProgram;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RegistrationDAOImpl implements RegistrationDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    @Override
    public List<Registration> getAll() throws Exception {
        return List.of();
    }

    @Override
    public boolean save(Registration registration) throws SQLException {
        return false;
    }

    @Override
    public boolean update(Registration registration) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteByPK(RegistrationId pk) throws SQLException {
        return false;
    }

    @Override
    public boolean save(Registration registration, Session session) throws SQLException {
        session.persist(registration);
        return true;
    }

    @Override
    public Registration get(int id, Session session) throws SQLException {
        try {
            return session.createQuery(
                            "FROM Registration r WHERE r.patient.id = :patientId", Registration.class)
                    .setParameter("patientId", id)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Failed to get Registration for patient ID: " + id, e);
        }
    }

    @Override
    public boolean update(Registration registration, Session session) throws SQLException {
        session.merge(registration);
        return true;
    }

    @Override
    public void delete(Registration existingRegistration, Session session) {
        try {
            session.remove(existingRegistration);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete Registration", e);
        }
    }

    @Override
    public List<TherapyProgram> getProgramsByPatientId(int patientId) {
        Session session = factoryConfiguration.getSession();
        Query<Registration> query = session.createQuery(
                "FROM Registration r WHERE r.patient.id = :pid", Registration.class);
        query.setParameter("pid", patientId);

        List<Registration> registrations = query.list();

        return registrations.stream()
                .map(Registration::getProgram)
                .collect(Collectors.toList());
    }
}
