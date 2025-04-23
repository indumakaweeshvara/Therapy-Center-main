package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.PatientDAO;
import org.example.entity.Patients;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    @Override
    public List<Patients> getAll() throws Exception {
        return List.of();
    }

    @Override
    public boolean save(Patients patients) throws SQLException {
        return false;
    }

    @Override
    public boolean update(Patients patients) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteByPK(String pk) throws SQLException {
        return false;
    }

    @Override
    public int getLastId(Session session) throws SQLException {
        String hql = "SELECT MAX(p.id) FROM Patients p";
        Query<Integer> query = session.createQuery(hql, Integer.class);
        Integer result = query.getSingleResult();
        return (result == null) ? 0 : result;
    }

    @Override
    public boolean save(Patients patient, Session session) throws SQLException {
        session.persist(patient);
        return true;
    }

    @Override
    public List<Patients> getAll(Session session) throws SQLException {
        String hql = "FROM Patients";
        Query<Patients> query = session.createQuery(hql, Patients.class);
        return query.list();
    }

    @Override
    public Patients get(int id, Session session) throws SQLException {
        return session.get(Patients.class, id);
    }

    @Override
    public boolean delete(Patients patient, Session session) throws SQLException {
        session.remove(patient);
        return true;
    }

    @Override
    public boolean update(Patients patient, Session session) throws SQLException {
        session.merge(patient);
        return true;
    }

    @Override
    public int getRemainingSessions(int patientId) {
        Session session = factoryConfiguration.getInstance().getSession();
        try {
            String hql = "SELECT p.remainingSessions FROM Patients p WHERE p.id = :patientId";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("patientId", patientId);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            session.close();
        }
    }

    @Override
    public Patients getPatientById(int patientId) {
        Session session = factoryConfiguration.getInstance().getSession();
        try {
            return session.get(Patients.class, patientId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }


}
