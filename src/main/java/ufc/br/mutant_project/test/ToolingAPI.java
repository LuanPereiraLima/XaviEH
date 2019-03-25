package ufc.br.mutant_project.test;

import org.gradle.tooling.*;

import java.io.File;

public class ToolingAPI
{
    private static final String GRADLE_INSTALLATION = "Program FilesGradle";
    private static final String GRADLE_PROJECT_DIRECTORY = "/home/loopback/mutationsTests/mockito-2.23.11/mockito-2.23.11";
    private static final String GRADLE_TASK = "test";

    private GradleConnector connector;

    public static void main(String[] args) {
        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(new File(GRADLE_PROJECT_DIRECTORY))
                .connect();

        try {
            connection
                .newBuild()
                .setStandardOutput(System.out)
                .setStandardError(System.out)
                .forTasks("test")
                .run();
        } finally {
            connection.close();
        }
    }
}