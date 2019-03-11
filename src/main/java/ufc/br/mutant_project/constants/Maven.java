package ufc.br.mutant_project.constants;

import java.util.Arrays;
import java.util.List;

public class Maven {
	public final static List<String> GOALS_PROJECT = Arrays.asList( "test" );
	public final static List<String> GOALS_PROJECT_DISABLE_CHECK = Arrays.asList(new String[] {"test", "-Dcheckstyle.skip"});
}
