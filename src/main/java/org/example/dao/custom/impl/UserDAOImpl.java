package org.example.dao.custom.impl;

import org.example.config.FactoryConfiguration;
import org.example.dao.custom.UserDAO;
import org.example.entity.Users;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private final FactoryConfiguration factoryConfiguration=FactoryConfiguration.getInstance();
    @Override
    public List<Users> getAllReceptionist() throws Exception {
        Session session = factoryConfiguration.getSession();
        Query<Users> query = session.createQuery("SELECT u from Users u where u.role='Receptionist'", Users.class);
        List<Users> usersList = query.list();
        session.close();
        return usersList;
    }

    @Override
    public List<Users> getAll() throws Exception {
        return List.of();
    }

    @Override
    public boolean save(Users users) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.persist(users);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean update(Users users) throws SQLException {
        Session session = factoryConfiguration.getSession();
        Transaction transaction =null;
        try {
            transaction = session.beginTransaction();

            Users usersById = session.get(Users.class, users.getId());

            if (usersById == null) {
                System.out.println("User not found with ID: " + users.getId());
                return false;
            }

            usersById.setUsername(users.getUsername());
            usersById.setFullname(users.getFullname());
            usersById.setEmail(users.getEmail());
            if (users.getPassword() != null && !users.getPassword().isEmpty()) {
                usersById.setPassword(users.getPassword());
            }
            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.out.println("User Not Updated");
            return false;
        }finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean deleteByPK(String pk) throws SQLException {
        Session session=factoryConfiguration.getSession();
        Transaction transaction=session.beginTransaction();
        try{
            Users user=session.get(Users.class, pk);
            if(user==null){
                return false;
            }
            session.remove(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        }finally{
            if(session!=null){
                session.close();
            }
        }
    }


}
