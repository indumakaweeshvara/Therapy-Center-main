package org.example.dao;

import org.example.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {
        if (daoFactory == null) {
            daoFactory = new DAOFactory();
        }
        return daoFactory;
    }

    public <T extends SuperDAO> T getDAO(DAOTypes daoType) {
        return switch(daoType){
            case LOGIN -> (T) new LoginDAOImpl();
            case USER -> (T) new UserDAOImpl();
            case THERAPIST -> (T) new TherapistDAOImpl();
            case PROGRAM -> (T) new TherapyProgramDAOImpl();
            case THERAPY_DETAIL -> (T) new TherapyDetailDAOImpl();
            case PATIENT -> (T) new PatientDAOImpl();
            case REGISTRATION -> (T) new RegistrationDAOImpl();
            case PAYMENT -> (T) new PaymentDAOImpl();
            case QUERY -> (T) new QueryDAOImpl();
            case THERAPY_SESSION -> (T) new TherapySessionDAOImpl();
            /*default -> null;*/
        };
    }
}
