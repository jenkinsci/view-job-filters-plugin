package hudson.views.test;

import hudson.model.*;
import hudson.scm.ChangeLogSet;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

public class BuildMocker<T extends AbstractBuild> {

    final T build;

    public BuildMocker(Class<T> jobClass) {
        this.build = Mockito.mock(jobClass);
    }

    public static BuildMocker<Build> build() {
        return new BuildMocker(Build.class);
    }

    public T getBuild() {
        return build;
    }

    public BuildMocker<T> desc(String desc) {
        when(build.getDescription()).thenReturn(desc);
        return this;
    }

    public BuildMocker<T> started(boolean started) {
        when(build.hasntStartedYet()).thenReturn(!started);
        return this;
    }

    public BuildMocker<T> result(Result result) {
        when(build.getResult()).thenReturn(result);
        return this;
    }

    public BuildMocker causes(Cause... causes) {
        when(build.getCauses()).thenReturn(asList(causes));
        return this;
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

    public BuildMocker<T> durationInSeconds(long seconds)  {
        when(build.getDuration()).thenReturn(seconds * 1000);
        return this;
    }

    public BuildMocker<T> durationInMillis(long millis)  {
        when(build.getDuration()).thenReturn(millis);
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

    public BuildMocker<T> changes(final ChangeLogSet.Entry... entries) {
        ChangeLogSet<ChangeLogSet.Entry> changes = mock(ChangeLogSet.class);
        when(changes.iterator()).thenAnswer((Answer<Iterator<ChangeLogSet.Entry>>) invocationOnMock -> asList(entries).iterator());
        when(build.getChangeSet()).thenReturn(changes);
        return this;
    }

    public BuildMocker<T> parameters(ParameterValue... values) {
        when(build.getAction(ParametersAction.class)).thenReturn(new ParametersAction(values));
        return this;
    }

    public T create() {
        return build;
    }


}
