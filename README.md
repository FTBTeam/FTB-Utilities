(LatCoreMC before)
CurseForge link:
http://minecraft.curseforge.com/projects/ftb-utilities

# Building

## Setting up

*These steps are required before doing anything else. This assumes you have git installed
on Linux, or on [Windows in the PATH](https://git-scm.com/download/win)*

1. Execute `git clone --recursive URL` - replace `URL` with [the "HTTPS clone URL" on the right of
the GitHub repository page](http://i.imgur.com/rg8pLgf.png)
2. Go into the cloned repository with `cd FTBUtilities`

## Linux server

1. Execute `./gradlew build` to begin the build process
2. The FTBUtilities jar file will be found in the `output` directory

## IntelliJ

*Some of these steps may take up to 5 minutes to complete*

1. [Open `build.gradle` in the repository as a project](http://i.imgur.com/7sKC752.png)
2. [Ensure "Use default gradle wrapper" and the correct path to the `build.gradle` file is selected](http://i.imgur.com/IRZ8IoX.png)
3. [Under "Gradle projects", navigate to and double-click on the "setupDecompWorkspace" task](http://i.imgur.com/VMHet6H.png)
4. After the task completes, [press the Refresh button in "Gradle projects"](http://i.imgur.com/v9IWjMU.png)
5. When ready to build, [navigate to and double-click on the "build" task](http://i.imgur.com/5ARUdoU.png)
6. The FTBUtilities jar file will be found in the `output` directory