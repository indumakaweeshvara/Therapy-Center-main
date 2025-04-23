package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.QueryDAO;
import org.example.entity.Therapist;
import org.hibernate.Session;

import java.util.List;

public class QueryDAOImpl implements QueryDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    @Override
    public List<Therapist> getTherapistsByPatientId(String patientId) throws Exception {
        try {
            Session session = factoryConfiguration.getSession();
            session.beginTransaction();

            List<Therapist> therapistList = session.createQuery("""
                    SELECT DISTINCT td.therapist 
                    FROM Registration r 
                    JOIN TherapyDetail td ON r.program.id = td.program.id 
                    WHERE r.patient.id = :pid
                """, Therapist.class)
                    .setParameter("pid", patientId)
                    .getResultList();

            session.getTransaction().commit();
            return therapistList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
