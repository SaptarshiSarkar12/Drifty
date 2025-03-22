# Architecture

## Project Structure

The project is organized into the following directories:

- `.github`: Contains the GitHub Actions workflows, issue and pull request templates, linter configuration files, and other GitHub-specific files.
- `CLI`: Maven child module containing the source code for the Drifty CLI.
- `GUI`: Maven child module containing the source code for the Drifty GUI.
- `Core`: Maven child module containing the shared code between the CLI and GUI.
- `Website`: Contains the source code for the Drifty website (A Next.js application).
- `config`: Contains the configuration files for building the native installers and executables.
- `Docker`: Contains the Dockerfile to build and run the Drifty application in a Docker container.
  - `dev`: Contains the Dockerfiles to build and run the Drifty application in a Docker container for development purposes.
    - `CLI`: Contains the Dockerfile to build and run the Drifty CLI executable in a Docker container for development purposes.
    - `GUI`: Contains the Dockerfile to build and run the Drifty GUI executable in a Docker container for development purposes.
    - `commons`: Contains the Dockerfiles for the base images used by the Drifty CLI and GUI Dockerfiles.
  - `prod`: Contains the Dockerfile to build and run the Drifty application in a Docker container for production purposes.
    - `CLI`: Contains the Dockerfile to build the Docker image for the Drifty CLI executable.
    - `GUI`: Contains the Dockerfile to build the Docker image for the Drifty GUI executable.

## Technologies Used

The Drifty project uses the following technologies:

- [**Java**](https://www.java.com/): The project is written in Java, which is a high-level, class-based, object-oriented programming language.
- [**JavaFX**](https://openjfx.io/): The project uses JavaFX as the GUI toolkit for Drifty GUI.
- [**Maven**](https://maven.apache.org/): The project uses Maven as the build automation and project management tool.
- [**GraalVM**](https://www.graalvm.org/): The project uses GraalVM to build native executables for the Drifty CLI and GUI.
- [**GluonFX Maven Plugin**](https://github.com/gluonhq/gluonfx-maven-plugin): The project uses the GluonFX Maven Plugin (which uses GraalVM under the hood) to build native executables for the Drifty GUI.
- [**Next.js**](https://nextjs.org/): The project uses Next.js to build the Drifty website.
- [**Docker**](https://www.docker.com/): The project uses Docker to containerize the Drifty application for development and production purposes.
- [**GitHub Actions**](https://docs.github.com/en/actions): The project uses GitHub Actions for CI/CD workflows.
