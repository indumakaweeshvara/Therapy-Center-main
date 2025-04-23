package org.example.dao.custom;

import org.example.dao.CrudDAO;
import org.example.entity.Therapist;
import org.hibernate.Session;


import java.sql.SQLException;

public interface TherapistDAO extends CrudDAO<Therapist,String> {

    Therapist get(int therapistId, Session session);
}
