package messenger.utils;

import org.hibernate.Session;

@FunctionalInterface
public interface DatabaseActionListener {

    void onAction(Session session);
}
