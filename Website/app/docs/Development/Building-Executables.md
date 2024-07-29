# Building Installer or Executable Binaries for Drifty

## Local Build

### Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Download](https://maven.apache.org/download.cgi#previous-stable-3-8-x-release) and [install](https://maven.apache.org/install.html) **Maven** (Maven v3.8.8 is required for building installer or executable binaries for Drifty GUI, locally)
- [GraalVM 21](https://www.graalvm.org/downloads/)
- [GCC](https://gcc.gnu.org/install/)

### Steps

> [!NOTE]
> Check if GraalVM is added to the system path by running `native-image --version` in the terminal.
> If the command is not recognized, add GraalVM `bin` directory to the system path.
> ```
> GRAALVM_HOME=<path-to-graalvm>
> PATH=$GRAALVM_HOME/bin:$PATH
> ```
> Replace `<path-to-graalvm>` with the actual path to GraalVM.

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
   - For Mac,
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
     - For Mac,
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
     - For Mac,
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
     - For Mac,
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
     - For Mac,
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
