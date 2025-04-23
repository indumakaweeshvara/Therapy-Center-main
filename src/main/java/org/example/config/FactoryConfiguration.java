package org.example.config;

import org.example.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Properties;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private SessionFactory sessionFactory;

    private FactoryConfiguration() {
        Configuration configuration = new Configuration();//add configuration file
        Properties properties = new Properties();//add properties

        /*add already created hibernate file to properties in current thread */
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("hibernate.properties"));//load properties file
        } catch (IOException e) {
            /*throw new RuntimeException(e);*/
            e.printStackTrace();
        }

        configuration.setProperties(properties);//add properties to configuration
        configuration.addAnnotatedClass(Users.class);  //add annotated class to configuration
        configuration.addAnnotatedClass(Patients.class);
        configuration.addAnnotatedClass(TherapyProgram.class);
        configuration.addAnnotatedClass(Registration.class);
        configuration.addAnnotatedClass(Payment.class);
        configuration.addAnnotatedClass(Therapist.class);
        configuration.addAnnotatedClass(TherapySession.class);
        configuration.addAnnotatedClass(TherapyDetail.class);
        sessionFactory = configuration.buildSessionFactory();//build session factory

    }

    public static FactoryConfiguration getInstance() {
        return (factoryConfiguration == null) ? factoryConfiguration = new FactoryConfiguration() : factoryConfiguration;
    }

    public Session getSession(){
        return sessionFactory.openSession();
    }

}
