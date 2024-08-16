# Quick Start

This is a quick start guide to get developers up and running with the project for development purposes.

## Clone the Project

> [!NOTE]
> You need to have Git installed on your machine to clone the project. If you don't have Git installed, you can download it from [here](https://git-scm.com/downloads).

Clone the Drifty repository to your local machine using the following command:

```bash
git clone git@github.com:SaptarshiSarkar12/Drifty.git
```

After the project has been cloned successfully, the **`Drifty`** directory will be created. Navigate into that directory.

## Drifty Application Development

### Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (**Recommended**)

### Install Dependencies

Install the dependencies required for the project using the following command:

```bash
mvn clean install
```

This command will install all the dependencies required for the maven project.

### Running the Project in IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Follow the below steps to run the project:
   - Open the `Drifty_CLI` Java class in the `CLI/src/main/java/main` directory and click on the run button. This will start the Drifty CLI application.
   - Open the `Drifty_GUI` Java class in the `GUI/src/main/java/main` directory and click on the run button. This will start the Drifty GUI application.
3. Make changes to the code and see them reflected in the application. Re-run the project after each change to see the updated output.

### Debugging Drifty Application

To debug the project, you can set breakpoints in the code and run the project in debug mode (by clicking on the debug button in IntelliJ IDEA). This will allow you to step through the code and inspect variables to identify and resolve issues.

## Drifty Website Development

### Prerequisites

- [Node.js](https://nodejs.org/en/download/)
- [npm](https://www.npmjs.com/get-npm)
- [WebStorm](https://www.jetbrains.com/webstorm/) (**Recommended**) (You can also use any other IDE of your choice)

### Install Dependencies

Navigate to the `Website` directory and install the dependencies required for the website using the following command:

```bash
npm ci
```

### Running the Website Locally (Development Mode)

To run the website locally in development mode, use the following command:

```bash
npm run dev
```

This will start the development server, and you can access the website at `http://localhost:3000` in your browser.
Make changes to the website code from your IDE and see them reflected in real-time in the browser.

### Debugging Drifty Website

To debug the website, you can use the browser's developer tools to inspect the elements, view console logs, and debug JavaScript code. You can also use the `console.log()` function to log messages to the console for debugging purposes.

## Contributing

If you would like to contribute to the project, please read the [Contributing Guidelines](../Contributing.md) for more information on how to get started.
