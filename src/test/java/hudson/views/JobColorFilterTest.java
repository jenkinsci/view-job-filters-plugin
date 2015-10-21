package hudson.views;

import hudson.model.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JobColorFilterTest {
    JobColorFilter filter;
    String includeExcludeType = AbstractIncludeExcludeJobFilter.IncludeExcludeType.includeMatched.toString();
    TopLevelJob job = mock(TopLevelJob.class);
    Run run = mock(Run.class);
    BallColor ballColor = BallColor.RED;

    private abstract class TopLevelJob  extends Job implements TopLevelItem {
        protected TopLevelJob(ItemGroup parent, String name) {
            super(parent, name);
        }
    }

    @Before
    public void setup(){
        when(job.getLastCompletedBuild()).thenReturn(run);
        filter = new JobColorFilter(BallColor.RED.toString(), includeExcludeType);
    }

    @Test
    public void shouldNotMatchWhenTopLevelItemIsNotAJob(){

        TopLevelItem item  = mock(TopLevelItem.class);

        boolean isMatch = filter.matches(item);

        assertFalse(isMatch);
    }

    @Test
    public void shouldMatchColorWhenRequestedColorMatchesJobColor() {

        when(run.getIconColor()).thenReturn(ballColor);

        boolean isMatch = filter.matches(job);

        assertTrue(isMatch);
    }

    @Test
    public void shouldNotMatchColorWhenRequestedColorDoesNotMatchJobColor() {

        when(run.getIconColor()).thenReturn(BallColor.ABORTED);

        boolean isMatch = filter.matches(job);

        assertFalse(isMatch);
    }

    @Test
    public void shouldNotMatchWhenThereIsNotALastCompletedBuild(){

        when(job.getLastCompletedBuild()).thenReturn(null);

        boolean isMatch = filter.matches(job);

        assertFalse(isMatch);
    }

    @Test
    public void ballColorStringShouldBeCaseInsensitive(){
        try {
            filter = new JobColorFilter(BallColor.RED.toString().toLowerCase(), includeExcludeType);
        } catch (IllegalArgumentException e) {
            fail("Ball color string should be case insensitive");
        }
    }
}