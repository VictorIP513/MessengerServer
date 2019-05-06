package messenger.dao;

import messenger.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserDao {

    private static final String LOGIN_COLUMN_NAME = "login";
    private static final String EMAIL_COLUMN_NAME = "email";

    @Autowired()
    private SessionFactory sessionFactory;

    public boolean isExistLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            return isExistField(session, LOGIN_COLUMN_NAME, login);
        }
    }

    public boolean isExistEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            return isExistField(session, EMAIL_COLUMN_NAME, email);
        }
    }

    private boolean isExistField(Session session, String columnName, String field) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);

        ParameterExpression<String> params = criteriaBuilder.parameter(String.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get(columnName), params));

        TypedQuery<User> query = session.createQuery(criteriaQuery);
        query.setParameter(params, field);

        List<User> queryResult = query.getResultList();
        return !queryResult.isEmpty();
    }
}
