# Building Installer or Executable Binaries for Drifty

## Generating GraalVM Metadata

> [!NOTE]
> This step is required only if you want to see your changes reflected in Drifty CLI or GUI executables.
> If you are only interested in building the installer or executable binaries, you can skip this step.

### Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Download](https://maven.apache.org/download.cgi#previous-stable-3-8-x-release) and [install](https://maven.apache.org/install.html) **Maven** (Maven v3.8.8 is required for generating GraalVM metadata for Drifty GUI)
- [GraalVM 21](https://www.graalvm.org/downloads/)

### Steps

1. Open the terminal and navigate to the project directory
2. Follow the below instructions to generate GraalVM metadata for Drifty CLI or GUI
   - For Drifty GUI,
     - Navigate to the `GUI` directory
       ```shell
       cd GUI
       ```
     - Run the below command to generate GraalVM metadata for Drifty GUI
       ```shell
       mvn gluonfx:runagent
       ```
   - For Drifty CLI,
     - Navigate to the `CLI` directory
       ```shell
       cd CLI
       ```
     - Run the below command to generate GraalVM metadata for Drifty CLI
       ```shell
       mvn -P generate-graalvm-metadata exec:exec@java-agent
       ```
3. Upon completion of the command, the GraalVM metadata will be generated in `src/main/resources/META-INF/native-image` directory of the respective project (`GUI` or `CLI`) directory.

## Local Build

### Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Download](https://maven.apache.org/download.cgi#previous-stable-3-8-x-release) and [install](https://maven.apache.org/install.html) **Maven** (Maven v3.8.8 is required for building installer or executable binaries for Drifty GUI, locally)
- [GraalVM 21](https://www.graalvm.org/downloads/)
- [GCC](https://gcc.gnu.org/install/)

### Steps

> [!NOTE]
> Check if GraalVM is added to the system path by running `native-image --version` in the terminal.
> If the command is not recognized, add the GraalVM `bin` directory to the system path.
>
> ```shell
> PATH=$GRAALVM_HOME/bin
> ```
>
> Set the following environment variable to point to your GraalVM installation directory.
>
> ```shell
> GRAALVM_HOME=<path-to-graalvm>
> ```
>
> Replace `<path-to-graalvm>` with the actual path to the GraalVM installation directory.

1. Open the terminal and navigate to the project directory
2. Assuming you have installed the necessary project dependencies, run the below command to generate the C object file required only for building executable binaries for Drifty GUI
   - For Linux,
     ```shell
     gcc -c config/missing_symbols.c -o config/missing_symbols-ubuntu-latest.o
     ```
   - For Windows,
     ```shell
     gcc -c config/missing_symbols.c -o config/missing_symbols-windows-latest.o
     ```
   - For macOS,
     ```shell
     gcc -c config/missing_symbols.c -o config/missing_symbols-macos-latest.o
     ```
     Replace `gcc` with the path to the GCC compiler if it is not in the system path.
3. Run the below command to build the installer or executable binaries
   - For Drifty GUI,
     - For Linux,
       ```shell
       mvn -P build-drifty-gui-for-ubuntu-latest gluonfx:build gluonfx:package -rf :GUI -U
       ```
     - For Windows,
       ```shell
       mvn -P build-drifty-gui-for-windows-latest gluonfx:build gluonfx:package -rf :GUI -U
       ```
     - For macOS,
       ```shell
       mvn -P build-drifty-gui-for-macos-latest gluonfx:build gluonfx:package -rf :GUI -U
       ```
   - For Drifty CLI,
     - For Linux,
       ```shell
       mvn -P build-drifty-cli-for-ubuntu-latest package
       ```
     - For Windows,
       ```shell
       mvn -P build-drifty-cli-for-windows-latest package
       ```
     - For macOS,
       ```shell
       mvn -P build-drifty-cli-for-macos-latest package
       ```
4. Upon completion of the build, the installer or executable binaries will be neatly organized in the directories listed below. The placeholder `{arch}` should be replaced with either `x86_64` or `aarch64`, depending on your system's architecture.
   - For Drifty GUI,
     - For Linux,
       ```shell
       GUI/target/gluonfx/{arch}-linux
       ```
     - For Windows,
       ```shell
       GUI/target/gluonfx/{arch}-windows
       ```
     - For macOS,
       ```shell
       GUI/target/gluonfx/{arch}-mac
       ```
   - For Drifty CLI,
     - For Linux,
       ```shell
       CLI/target/CLI/linux
       ```
     - For Windows,
       ```shell
       CLI/target/CLI/windows
       ```
     - For macOS,
       ```shell
       CLI/target/CLI/mac
       ```
5. You can now run the installer or executable binaries to use the application.
6. To remove the generated files, run the below command
   ```shell
   mvn clean
   ```

## Docker Build

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Steps

1. Open the terminal and navigate to the project directory
2. Follow the below instructions to build and start the Drifty application in a Docker container
   - For Drifty GUI,
     - For Linux and Windows users,
       - Run the below command to add access to the X server (required for GUI applications running in Docker)
         ```shell
         xhost +local:docker
         ```
       - Run the below command to build and start the Drifty GUI native executable in a Docker container
         ```shell
         docker compose run gui
         ```
     - For macOS users, please follow [these instructions](macOS%20Docker%20Build%20Instructions.md)
   - For Drifty CLI,
     Run the below command to build and start the Drifty CLI native executable in a Docker container
     ```shell
     docker compose run cli
     ```
