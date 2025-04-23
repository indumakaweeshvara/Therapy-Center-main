package org.example.dao.custom;

import org.example.dao.CrudDAO;
import org.example.entity.Registration;
import org.example.entity.RegistrationId;
import org.example.entity.TherapyProgram;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

public interface RegistrationDAO extends CrudDAO<Registration, RegistrationId> {
    boolean save(Registration registration, Session session) throws SQLException;

    Registration get(int id, Session session) throws SQLException;

    boolean update(Registration registration, Session session) throws SQLException;

    void delete(Registration existingRegistration, Session session);
    List<TherapyProgram> getProgramsByPatientId(int patientId);
}
