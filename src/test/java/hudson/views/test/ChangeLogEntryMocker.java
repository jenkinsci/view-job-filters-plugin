package hudson.views.test;

import hudson.model.User;
import hudson.scm.ChangeLogSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeLogEntryMocker<T extends ChangeLogSet.Entry> {

    T entry;

    public ChangeLogEntryMocker(Class<T> clazz) {
        this.entry = mock(clazz);
    }

    public static ChangeLogEntryMocker<ChangeLogSet.Entry> entry() {
       return new ChangeLogEntryMocker<ChangeLogSet.Entry>(ChangeLogSet.Entry.class);
    }

    public static ChangeLogSet.Entry entry(User user) {
        return entry().user(user).create();
    }

    public ChangeLogEntryMocker user(User user) {
        when(entry.getAuthor()).thenReturn(user);
        return this;
    }

    public T create() {
        return entry;
    }
}
