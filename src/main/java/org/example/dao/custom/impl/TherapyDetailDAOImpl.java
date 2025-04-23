package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.TherapyDetailDAO;
import org.example.entity.Therapist;
import org.example.entity.TherapyDetail;
import org.example.entity.TherapyDetailId;
import org.example.entity.TherapyProgram;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TherapyDetailDAOImpl implements TherapyDetailDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    @Override
    public List<TherapyDetail> getAll() throws Exception {
        Session session = factoryConfiguration.getSession();
        try {
            Query<TherapyDetail> query = session.createQuery("FROM TherapyDetail", TherapyDetail.class);
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
    public boolean save(TherapyDetail therapyDetail) throws SQLException {
        return false;
    }


    @Override
    public boolean update(TherapyDetail therapyDetail) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Load existing therapy detail to maintain relationships
            TherapyDetail existingDetail = session.get(TherapyDetail.class, therapyDetail.getTherapyDetailId());

            if (existingDetail != null) {
                // Only update the note as the composite key parts shouldn't change
                existingDetail.setNote(therapyDetail.getNote());

                session.merge(existingDetail);
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
    public boolean deleteByPK(TherapyDetailId pk) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            TherapyDetail therapyDetail = session.get(TherapyDetail.class, pk);

            if (therapyDetail != null) {
                session.remove(therapyDetail);
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
    public boolean saveWithReferences(TherapyDetail therapyDetail, int therapistId, String programId) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Get references within the same session
            Therapist therapist = session.getReference(Therapist.class, therapistId);
            TherapyProgram program = session.getReference(TherapyProgram.class, programId);

            therapyDetail.setTherapist(therapist);
            therapyDetail.setProgram(program);

            session.persist(therapyDetail);
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
}
