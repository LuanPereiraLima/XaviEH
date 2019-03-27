## Mutation Program Test

#### Operations Coverage

- [x] Coverage General
- [x] Coverage EH
                                       
#### Operations Mutants

- [x] CBD - Catch Block Deletion
- [x] CBI - Catch Block Insertion
- [x] CRE - Catch and Rethrow Exception
- [x] FBD - Finally Block Deletion
- [x] CBR - Catch Block Replacement
- [x] PTL - Placing Try Block Late

#### Pendencies

- [x] Automate the procurement of projects through Git
- [x] JaCoCo to cover tests
- [ ] Run with Gradlew

#### Usage

- use **mvn install**
- Enter in **/target**
- Execute: **java -jar project-mutant-2.0.0-jar-with-dependencies.jar** for run the program.
- The file config.properties (which is inside the folder (**/target/FilesNeedToRun**) - **be sure to copy the files from this folder to the directory where the executable jar file is located.**), has all the necessary parameters for the project to work correctly, they are:
  - **homeMaven**: path to the maven installed on your computer.
  - **urlMutations**: path where the mutants and information will be created (always finish the path with a slash at the end. **/**).
  - **projectsFile**: name of the file containing the projects that will be used. The same is composed of a url of git and some additional parameters, they are:
    - **-v**: defines the version of the project in question. (**required**)
    - **-c**: ID of commit of project.
    - **-m**: Submodule of project.
    - **-p**: Type of project (Maven or NotMaven) (parameter **g** to not maven project)
    - **-pp**: Path of default file project location. (Default: **/src/main/java**)
    - **Obs: The parameters are optionals, exception for [-v]** 


###### PARAMS: 
- **noTestProject**: The project will not be test before 
- **outputFile**: The console output will be saved to a local file.
- **noCloneRepository**: The file projects will be run without cloning the git repository.
- **noVerifyProject**: Before the project is analyzed it is checked if it has already been run before, if positive, it will be skipped. This option causes the project to not be parsed.
- Type of executions: 
  - **ExecuteMutationsAndCoverage**: The mutants of the projects will be created and also their coverage using JaCoCo.
  - **ExecuteEstatisticsCoverageEH**: 
    - All Raisings.
    - Raisings of Programmer Defined Exception.
    - Raisings of Non Programmer Defined Exception.
    - All Handlings
    - Handlings of Programmer Defined Exception.
    - Handlings of Non Programmer Defined Exception.
  - **ExecuteEstatisticsCoverageEH2**: Get informations about exception of projects.
  - **ExecuteCloneAndRunTestsWithJaCoCo**: Make only tests and JaCoCo report of projects.
