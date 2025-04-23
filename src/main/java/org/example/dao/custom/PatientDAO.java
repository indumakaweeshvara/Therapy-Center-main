package org.example.dao.custom;

import org.example.dao.CrudDAO;
import org.example.entity.Patients;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

public interface PatientDAO extends CrudDAO<Patients,String> {
    int getLastId(Session session) throws SQLException;
    boolean save(Patients patient, Session session) throws SQLException;

    List<Patients> getAll(Session session) throws SQLException;

    Patients get(int id, Session session) throws SQLException;

    boolean delete(Patients patient, Session session) throws SQLException;

    boolean update(Patients patient, Session session) throws SQLException;
    int getRemainingSessions(int patientId);

    Patients getPatientById(int patientId);
}
