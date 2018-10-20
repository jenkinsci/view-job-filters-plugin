package hudson.views.test;

import hudson.cli.BuildCommand;
import hudson.model.Build;
import hudson.model.Cause;
import hudson.model.Result;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CauseMocker {

    public static Cause.UserIdCause userIdCause(String id, String name) {
        Cause.UserIdCause userIdCause = mock(Cause.UserIdCause.class);
        when(userIdCause.getUserId()).thenReturn(id);
        when(userIdCause.getUserName()).thenReturn(name);
        return userIdCause;
    }

    public static Cause.UserCause userCause(String name) {
        Cause.UserCause userCause = mock(Cause.UserCause.class);
        when(userCause.getUserName()).thenReturn(name);
        return userCause;
    }

    public static BuildCommand.CLICause cliCause(String id, String name) {
        BuildCommand.CLICause cliCause = mock(BuildCommand.CLICause.class);
        when(cliCause.getUserId()).thenReturn(id);
        when(cliCause.getUserName()).thenReturn(name);
        return cliCause;
    }

}
