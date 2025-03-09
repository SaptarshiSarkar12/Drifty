# Drifty Web

## Features

- 🚀 **Built with Next.js** – Fast, server-rendered React framework.
- 🎨 **Styled with Tailwind CSS** – Utility-first approach for easy customization.
- 📱 **Fully Responsive** – Works seamlessly across all devices.

## Getting Started

### 1. Clone the repository

```sh
git clone https://github.com/SaptarshiSarkar12/Drifty.git
```

### 2. Navigate into the project directory

```sh
cd Drifty
```

### 3. Install dependencies

```sh
npm install
```

#### Potential Issues & Fixes:

- If you encounter `ERR! could not resolve dependency`, try deleting `node_modules` and `package-lock.json` before reinstalling:
  ```sh
  rm -rf node_modules package-lock.json
  npm install
  ```
- Ensure you're using the correct Node.js version. You can check your version with:
  ```sh
  node -v
  ```

### 4. Start the development server

```sh
npm run dev
```

#### Potential Issues & Fixes:

- If you see an error related to ports being in use, stop any conflicting process:
  ```sh
  lsof -i :3000  # Find the process using port 3000
  kill -9 <PID>   # Replace <PID> with the actual process ID
  ```
- If changes are not reflecting, restart the server and clear Next.js cache:
  ```sh
  rm -rf .next && npm run dev
  ```

This will start the app on [`http://localhost:3000`](http://localhost:3000).

## Contributing

We welcome contributions! Follow these steps to get started:

### Contribution Steps

1. [Fork the repository](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo) on GitHub.
2. Clone your forked repository:
   ```sh
   git clone https://github.com/<username>/Drifty.git
   cd Drifty
   ```
   > **NOTE:** Your github username should be updated in the command above.
3. Create a new branch:
   ```sh
   git checkout -b feature-name
   ```
4. Make your changes and sign your commits:
   ```sh
   git commit -S -m "Add new feature"
   ```
5. Push your branch to GitHub:
   ```sh
   git push origin feature-name
   ```
6. Follow the [Pre-Submission Checklist](#pre-submission-checklist) and [Open a pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request-from-a-fork) on GitHub.

### Pre-Submission Checklist

Before creating a pull request, ensure you follow these steps to minimize review issues:

1. **Build the project** to catch any build-related errors:
   ```sh
   npm run build
   ```
2. **Check for linting issues** and auto-fix where possible:
   ```sh
   npm run lint --fix
   ```
3. **Run Prettier** to format your code:
   ```sh
   npx prettier --write .
   ```
4. **Test the application** to confirm everything works as expected:
   ```sh
   npm start
   ```

### Contribution Guidelines

- Follow the existing code style and naming conventions.
- Keep your pull requests focused and concise.
- Ensure your changes do not break existing functionality.
- Test thoroughly before submitting.

## Deployment

This web app can be deployed on platforms like Vercel, Netlify, or any cloud provider supporting Next.js.

### Deploy on Vercel

```sh
npm run build
vercel deploy
```

## License

This project is licensed under the Apache 2.0 License. See the [LICENSE](../LICENSE) file for details.

## Support

If you encounter any issues, feel free to open an [issue](https://github.com/SaptarshiSarkar12/Drifty/issues) in the repository.

---

Happy coding! 🚀
