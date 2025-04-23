module Mental.Health.Therapy.Center {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.sql;
    requires net.sf.jasperreports.core;
    requires java.mail;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires bcrypt;

    opens org.example.entity to org.hibernate.orm.core;
    opens org.example.view.tdm to javafx.base;
    opens org.example.config to jakarta.persistence;

    opens org.example.controller to javafx.fxml;


    exports org.example;
    exports org.example.view.tdm;
}