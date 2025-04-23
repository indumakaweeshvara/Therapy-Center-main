package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.TherapyProgramDAO;
import org.example.entity.TherapyProgram;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TherapyProgramDAOImpl implements TherapyProgramDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    @Override
    public List<TherapyProgram> getAll() throws Exception {
        Session session = factoryConfiguration.getSession();
        try {
            Query<TherapyProgram> query = session.createQuery("FROM TherapyProgram", TherapyProgram.class);
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
    public boolean save(TherapyProgram therapyProgram) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.persist(therapyProgram);
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
    public boolean update(TherapyProgram program) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Load existing entity to preserve relationships
            TherapyProgram existingProgram = session.get(TherapyProgram.class, program.getProgramId());

            if (existingProgram != null) {
                existingProgram.setProgramName(program.getProgramName());
                existingProgram.setDuration(program.getDuration());
                existingProgram.setProgramCost(program.getProgramCost());
                existingProgram.setProgramDescription(program.getProgramDescription());

                session.merge(existingProgram);
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
            TherapyProgram program = session.get(TherapyProgram.class, pk);

            if (program != null) {
                session.remove(program);
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
    public boolean exists(String programId) throws SQLException {
        Session session = factoryConfiguration.getSession();
        try {
            TherapyProgram program = session.get(TherapyProgram.class, programId);
            return program != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public TherapyProgram getProgram(String programId) throws SQLException {
        Session session = factoryConfiguration.getSession();
        try {
            TherapyProgram program = session.get(TherapyProgram.class, programId);
            return program;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


}
