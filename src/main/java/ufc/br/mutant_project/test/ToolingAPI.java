package ufc.br.mutant_project.test;

import java.io.File;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnectionException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProgressListener;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;

public class ToolingAPI {
    private static final String GRADLE_INSTALLATION = "C:\\Program Files\\Gradle";
    private static final String GRADLE_PROJECT_DIRECTORY = "/home/loopback/mutationsTests/mockito-2.23.11/mockito-2.23.11";
    private static final String GRADLE_TASK = "test";

    private GradleConnector connector;

    public ToolingAPI(String gradleInstallationDir, String projectDir)
    {
        connector = GradleConnector.newConnector();
        //connector.useInstallation(new File(gradleInstallationDir));
        connector.forProjectDirectory(new File(projectDir));
    }

    public void executeTask(String... tasks)
    {
        ProjectConnection connection = connector.connect();
        BuildLauncher build = connection.newBuild();
        build.addProgressListener((ProgressListener) progressEvent -> System.out.println(progressEvent.getDescription()));
        build.setStandardOutput(System.out);
        build.setStandardError(System.out);
        build.forTasks(tasks);

        build.run(new ResultHandler<Void>() {
            @Override
            public void onComplete(Void aVoid) {
                System.out.println(aVoid);
            }

            @Override
            public void onFailure(GradleConnectionException e) {
                System.out.println(e);
            }
        });

        connection.close();
    }

    public static void main(String[] args)
    {
        ToolingAPI toolingAPI = new ToolingAPI(GRADLE_INSTALLATION, GRADLE_PROJECT_DIRECTORY);
        toolingAPI.executeTask(GRADLE_TASK);
    }
}
