package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.TherapistDAO;
import org.example.entity.Therapist;
import org.example.entity.TherapyProgram;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TherapistDAOImpl implements TherapistDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();

    @Override
    public List<Therapist> getAll() throws Exception {
        Session session = factoryConfiguration.getSession();
        try {
            Query<Therapist> query = session.createQuery("FROM Therapist", Therapist.class);
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
    public boolean save(Therapist therapist) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.persist(therapist);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean update(Therapist therapist) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            // First load the existing therapist to ensure we don't lose relationships
            Therapist existingTherapist = session.get(Therapist.class, therapist.getId());

            if (existingTherapist != null) {
                existingTherapist.setName(therapist.getName());
                existingTherapist.setEmail(therapist.getEmail());
                existingTherapist.setPhone(therapist.getPhone());
                existingTherapist.setSpecialization(therapist.getSpecialization());

                session.merge(existingTherapist);
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean deleteByPK(String pk) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            int therapistId = Integer.parseInt(pk);
            Therapist therapist = session.get(Therapist.class, therapistId);

            if (therapist != null) {
                session.remove(therapist);
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    @Override
    public Therapist get(int therapistId, Session session) {
        return session.get(Therapist.class, therapistId);
    }
}
