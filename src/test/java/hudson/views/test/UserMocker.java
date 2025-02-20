package hudson.views.test;

import hudson.model.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserMocker<T extends User> {

    final T user;

    public UserMocker(Class<T> clazz) {
        this.user = mock(clazz);
    }

    public static UserMocker<User> user() {
        return new UserMocker<>(User.class);
    }

    public static User user(String id, String name) {
        return user().id(id).name(name).create();
    }

    public UserMocker<T> id(String id) {
        when(user.getId()).thenReturn(id);
        return this;
    }

    public UserMocker<T> name(String name) {
        when(user.getFullName()).thenReturn(name);
        return this;
    }

    public T create() {
        return user;
    }
}
