package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.config.FactoryConfiguration;


public class Main extends Application {
    private static FactoryConfiguration factory;
    public static void main(String[] args) {
        /*factory = FactoryConfiguration.getInstance();
        Session session = factory.getSession();

        Users user1 = new Users();

        user1.setRole("Admin");
        user1.setUsername("admin");
        user1.setFullname("Praneeth Fernando");
        user1.setEmail("admin@gmail.com");


        user1.setPassword(BCrypt.withDefaults().hashToString(12, "685845".toCharArray()));

        Users user = new Users();

        user.setRole("Receptionist");
        user.setUsername("vinod");
        user.setFullname("Vinod Fernando");
        user.setEmail("vinodfernando048@gmail.com");


        user.setPassword(BCrypt.withDefaults().hashToString(12, "685845".toCharArray()));

        try {
            Transaction transaction = session.beginTransaction();
            session.persist(user1);
            session.persist(user);
            transaction.commit();
        } catch (Exception e) {
            System.out.println("Failed to save user");
            e.printStackTrace();
            throw new RuntimeException(e);

        }



        session.close();*/


        launch(args);

        /*Session session=FactoryConfiguration.getInstance().getSession(); //1st level cache ex-1

        Users vinod = session.get(Users.class,1 );
        System.out.println(vinod);

        Users vinod1 = session.get(Users.class, 1);
        System.out.println(vinod1);

        session.close();*/

        /*Session session=FactoryConfiguration.getInstance().getSession(); //2nd level cache
        Users vinod = session.get(Users.class,1 );
        System.out.println(vinod);
        session.close();
        Session session1=FactoryConfiguration.getInstance().getSession();
        Users vinod1 = session1.get(Users.class, 1);
        System.out.println(vinod1);*/




    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Serenity Therapy Center");
        Image image = new Image(getClass().getResourceAsStream("/assets/icons/icons8-alzheimer-96.png"));
        stage.getIcons().add(image);
        stage.setScene(scene);
        stage.show();
    }
}