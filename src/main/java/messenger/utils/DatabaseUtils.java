package messenger.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

@SuppressWarnings("unused")
public class DatabaseUtils {

    @Autowired
    private SessionFactory sessionFactory;

    public <T> boolean checkExistenceObject(Class<T> tableClassType, String columnName, Object object) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            return isExistField(session, tableClassType, columnName, object);
        }
    }

    public <T> T getUniqueObjectByField(Class<T> tableClassType, String columnName, Object object) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<T> queryResult = getObjectsByField(session, tableClassType, columnName, object);
            if (queryResult.isEmpty()) {
                return null;
            }
            return queryResult.get(0);
        }
    }

    public <T> List<T> getAllRecordsFromTable(Class<T> tableClassType) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            return getAllRecordsFromTable(session, tableClassType);
        }
    }

    public void saveObject(Object object) {
        openSessionAndExecuteAction(session -> session.save(object));
    }

    public void updateObject(Object object) {
        openSessionAndExecuteAction(session -> session.update(object));
    }

    private void openSessionAndExecuteAction(DatabaseActionListener databaseActionListener) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            databaseActionListener.onAction(session);
            session.getTransaction().commit();
        }
    }

    private <T> List<T> getAllRecordsFromTable(Session session, Class<T> tableClassType) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(tableClassType);
        criteriaQuery.from(tableClassType);
        return session.createQuery(criteriaQuery).getResultList();
    }

    private <T> List<T> getObjectsByField(Session session, Class<T> tableClassType, String columnName, Object field) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(tableClassType);
        Root<T> root = criteriaQuery.from(tableClassType);
        criteriaQuery.select(root);

        ParameterExpression<Object> params = criteriaBuilder.parameter(Object.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get(columnName), params));

        TypedQuery<T> query = session.createQuery(criteriaQuery);
        query.setParameter(params, field);

        return query.getResultList();
    }

    private <T> boolean isExistField(Session session, Class<T> tableClassType, String columnName, Object field) {
        List<T> queryResultList = getObjectsByField(session, tableClassType, columnName, field);
        return !queryResultList.isEmpty();
    }
}
