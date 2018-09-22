package hudson.views.test;

import hudson.model.*;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Mockito.*;

public class BuildMocker<T extends Build> {

    T build;

    public BuildMocker(Class<T> jobClass) {
        this.build = Mockito.mock(jobClass);
    }

    public static BuildMocker<Build> build() {
        return new BuildMocker(Build.class);
    }

    public BuildMocker<T> startTime(String startTime) throws ParseException {
        Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
        Calendar timestamp = GregorianCalendar.getInstance();
        timestamp.setTime(time);

        when(build.getTimeInMillis()).thenReturn(time.getTime());
        when(build.getStartTimeInMillis()).thenReturn(time.getTime());
        when(build.getTime()).thenReturn(time);
        when(build.getTimestamp()).thenReturn(timestamp);
        return this;
    }

    public BuildMocker<T> durationInMinutes(long minutes)  {
        when(build.getDuration()).thenReturn(minutes * 60 * 1000);
        return this;
    }

    public BuildMocker<T> building(boolean building)  {
        when(build.isBuilding()).thenReturn(building);
        return this;
    }

    public BuildMocker<T> previousBuild(Build previousBuild) {
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        return this;
    }

    public T create() {
        return build;
    }
}
