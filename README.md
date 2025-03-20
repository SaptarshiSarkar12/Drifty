<a href="https://drifty.vercel.app/">
    <p align="center">
        <img src="https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/92b11509-2115-4f80-8188-19821b258332" alt="Drifty Banner with App Icon">
    </p>
</a>

<p align="center">
    <a href="https://github.com/SaptarshiSarkar12/Drifty/releases/latest/"><img src="https://img.shields.io/github/v/release/SaptarshiSarkar12/Drifty?color=%23FFFF0g&amp;label=Drifty" alt="Release Version"></a>
    <a href="https://github.com/SaptarshiSarkar12/Drifty/blob/master/LICENSE"><img src="https://img.shields.io/github/license/SaptarshiSarkar12/Drifty" alt="License"></a>
    <a href="https://github.com/SaptarshiSarkar12/Drifty/releases/latest/"><img src="https://img.shields.io/github/downloads/SaptarshiSarkar12/Drifty/total" alt="Total No. Of Downloads of Drifty"></a>    
</p>

<p align="center">
    <a href="https://github.com/SaptarshiSarkar12/Drifty/actions/workflows/build.yml"><img src="https://github.com/SaptarshiSarkar12/Drifty/actions/workflows/build.yml/badge.svg" alt="Build Workflow Status"/></a> 
    <a href="https://drifty.vercel.app/"><img src="https://img.shields.io/github/deployments/SaptarshiSarkar12/Drifty/production?logo=Vercel&label=Website%20Deployment" alt="Vercel Website Deployment Status"></a>
</p>

<p align="center">
    <a href="https://github.com/SaptarshiSarkar12/Drifty/stargazers"><img src="https://img.shields.io/github/stars/SaptarshiSarkar12/Drifty?label=Leave%20a%20star&amp;style=social" alt="GitHub Stargazers for Drifty"></a> 
    <a href="https://twitter.com/SSarkar2007"><img src="https://img.shields.io/twitter/follow/SSarkar2007?style=social" alt="Follow us on Twitter"></a> 
    <a href="https://discord.gg/DeT4jXPfkG"><img src="https://img.shields.io/discord/1034035416300519454?label=Discord&amp;logo=discord" alt="Discord Server"></a>
</p>

## About 🔥

- [**Drifty**](https://github.com/SaptarshiSarkar12/Drifty/) is an **Open-Source** Interactive File downloader system developed in _Java_.
- It offers both **Command-line Interface** (_CLI_) and **Graphical User Interface** (_GUI_) modes, providing flexibility and ease of use for various user preferences.

## Demo 🎥

See the video below to know how to use the application. For any help, you can open an [`issue`](https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose/).

<details>
<summary>YAML configuration for Batch Downloading via Drifty CLI</summary>

For **Batch Downloading** via **Drifty CLI**, the path to a YAML file has to be provided to it.
It should have the following structure :

```yaml
links: ["", ""] # [REQUIRED] - Links to the files to be downloaded
# Below parameters are OPTIONAL
fileNames: [""] # Will be auto-retrieved if not provided
# If the directory is not provided, the files will be downloaded in the default downloads folder.
directory: [""] # Use 'directory' when all the files to be downloaded in the same folder.
directories: [""] # Use 'directories' when the download folder is different for each of the files
```

</details>

### Drifty GUI

https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/aa7ea548-f312-4345-a053-d3ee13e67c8e

### Drifty CLI

https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/8a32ca07-1922-4c21-895d-44164bec9d76

## Tech Stack 🛠️

The _Application_ **Drifty** is built using the following technologies :

- [Java](https://www.oracle.com/java/#rc30p1) — A programming language for building machine-independent applications.

The [_Website_](https://drifty.vercel.app/) of **Drifty** is built using the following technologies :

- [Next.js](https://nextjs.org/) — A JavaScript framework for building server-rendered React applications.
- [Tailwind CSS](https://tailwindcss.com/) — A utility-first CSS framework.

## Join the Drifty Testing Program 🚀

Drifty is released in _4 phases_, namely **Alpha**, **Beta**, **Release Candidate**, and **Stable**. If you want to test the unstable (**Alpha** or **Beta**) or the mostly stable versions (**Release Candidate**), you can download the executables for the respective phases from the [`Drifty website`](https://drifty.vercel.app/download) or try the Docker images for the respective phases. </br>
If you find any issues during the testing period, please open an [`issue`](https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose) or join our [`Discord Server`](https://discord.gg/DeT4jXPfkG) to share your feedback.

## Quickstart 🚀🚀

For **Users** :

### Using Drifty Application executable 📦

1. Visit the [`Drifty website`](https://drifty.vercel.app/) </p>
   ![Homepage of Drifty Website (Light Mode)](https://github.com/user-attachments/assets/2194dfd6-4f39-45d9-a287-eb35c534bc33)
2. Click on [`Get Started or Download`](https://drifty.vercel.app/download) </p>
   ![Download Button in Drifty Website (Dark mode) pointed out](https://github.com/user-attachments/assets/d97a6142-55eb-43b7-91e9-96537f6b5507)
3. The latest released version of Drifty will be selected by default. You can also select other previous versions from the dropdown menu. You can download the executable for your operating system by clicking on the **Download** icon next to the file you wish to download. Check the operating system icons to ensure you are downloading the correct file. </p>

   ![Download page of Drifty showing latest release](https://github.com/user-attachments/assets/52ad60e4-0361-40c0-90be-8857bd09e7b8)
   ![Download page of Drifty showing available versions](https://github.com/user-attachments/assets/61e58316-4e0b-4d6c-ba15-c38205beccd4)

4. Navigate to the Downloads folder and execute the downloaded file. If you are installing Drifty GUI, complete the steps in the macOS or Windows MSI installer wizard that appears on your screen. </p>
5. Check out the [`Demo`](https://github.com/SaptarshiSarkar12/Drifty?tab=readme-ov-file#demo-) section for examples of how to use the application. </p>
6. If you like the project, please leave a [`star`](https://github.com/SaptarshiSarkar12/Drifty/stargazers) on GitHub. </p>
7. For any issues or feature requests, you can join our [`Discord Server`](https://discord.gg/DeT4jXPfkG) and share your feedback.

### Using Drifty Application via Docker 🐋

> [!TIP]
>
> - To download files to a specific local directory, you need to mount that directory as a volume for Drifty.
>   For example, if you want to download the file to your `home/username/Downloads` directory, then, you need to use the volume flag `-v /home/username/Downloads:/root/Downloads` with the docker command.
> - You can also mount `-v /home/username/.drifty:/root/.drifty` to enable the docker container to store Drifty's data in your local directory, which will make Drifty initialize much faster 🚀⚡.
> - For **Linux**, we recommend using **Docker Engine** directly instead of running images with **Docker Desktop**. To do this, add **_`sudo`_** before the docker commands.
> - If you want to try unstable (**Alpha** or **Beta**) [**_Alpha releases are not available as of now_**] or the mostly stable versions (**Release Candidate**), you need to use the respective docker image tags. For example, to use the **Beta** version of Drifty, you need to use the docker image tag `beta` like `ghcr.io/saptarshisarkar12/drifty-cli:beta` or `ghcr.io/saptarshisarkar12/drifty-gui:beta`.

> [!IMPORTANT]
>
> - To run Drifty GUI docker image, you need to do the following 👇
>   - For **Linux and Windows**, please run `xhost +local:docker` before running the GUI docker image.
>   - For **macOS**, please follow [these instructions](Docker/macOS%20Docker%20Build%20Instructions.md).

1. Pull the Docker image for Drifty using the below command -  
   **For Drifty CLI**,
   ```bash
   docker pull ghcr.io/saptarshisarkar12/drifty-cli:master
   ```
   **For Drifty GUI**,
   ```bash
   docker pull ghcr.io/saptarshisarkar12/drifty-gui:master
   ```
2. Run the docker image using the below command -  
   **For Drifty CLI**,
   ```bash
   docker run ghcr.io/saptarshisarkar12/drifty-cli:master
   ```
   **For Drifty GUI**,
   ```bash
   docker run -e DISPLAY=$DISPLAY --net=host -v /tmp/.X11-unix:/tmp/.X11-unix ghcr.io/saptarshisarkar12/drifty-gui:master
   ```
3. Drifty will open, and you can now use it 🎉!

For **Contributors** :

1. Go to the [`project link on GitHub`](https://github.com/SaptarshiSarkar12/Drifty), [**`fork`**](https://github.com/SaptarshiSarkar12/Drifty/fork) this repository and clone your fork into your local directory by running this command in your terminal.
   ```bash
   git clone git@github.com:SaptarshiSarkar12/Drifty.git
   ```
   ![Clone Drifty](https://user-images.githubusercontent.com/105960032/194497334-856c610e-39cd-4538-a998-18afb10dac04.gif) </p>
2. Open an [`issue`](https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose) ❕ describing the changes you want to make. </p>
3. Make the changes in an IDE (preferably [`Intellij Idea`](https://www.jetbrains.com/idea/)) and open a Pull Request. Ensure that you have linked the issue to the Pull Request.
4. The Pull Request will be reviewed by the maintainers and merged if approved. ✔ </p>
5. Leave a [`star`](https://github.com/SaptarshiSarkar12/Drifty/stargazers) ⭐ on GitHub if you liked the project. </br>
6. You may join our [`Discord Server`](https://discord.gg/DeT4jXPfkG) to discuss on the changes that you want to bring.

### Using Docker 🐋 for Development 🛠️

> [!IMPORTANT]  
> For **Linux and Windows**, please run `xhost +local:docker` before running the GUI docker image.  
> For **macOS**, please follow [these instructions](Docker/macOS%20Docker%20Build%20Instructions.md) to run Drifty GUI docker image.

1. To start **Drifty GUI** and **Drifty CLI** with your modified source code, run `docker compose run gui` and `docker compose run cli` respectively.
2. The Drifty app will show up on the screen, and you can test it 🎉!

## Safety warning ⚠️ while Downloading ⬇️

![image](https://user-images.githubusercontent.com/58129377/193471489-87ee10a0-f719-47ef-9d46-e5b71c611d4b.png)

It is because Microsoft Defender could not verify it is safe as it is not commonly downloaded. Feel free to proceed with the following steps:

- Click on the three dots.
- Click on keep.

  ![image](https://user-images.githubusercontent.com/58129377/193471652-d88981c3-d903-406f-bc06-53cf77db9bf6.png)

- Click **`Show More`**.
- Click **`Keep anyway`**.

  ![image](https://user-images.githubusercontent.com/58129377/193471671-e047003c-95e7-43ed-8d37-d3f401b36164.png)

- Now it is downloaded, you can proceed.🎉

### Report the file as safe ✔️

You can also provide your feedback to Microsoft Defender by reporting it to be Safe, by following the below steps:

- Click on three dots.
- Click on **`Report this file as safe`** instead of clicking on **`Keep`**.

  ![image](https://user-images.githubusercontent.com/58129377/193471838-63ba50ba-e303-48b4-b7c1-b71e6c0663e7.png)

- It will take you to **`Report a download`** page. You can provide your feedback there.😄

## Safety warning ⚠️ while Installing

- For Windows, click on **More Info** and then **Run Anyway** as present on the screen below:
  |||
  |--|--|
  | ![Windows Defender - More Info screen](https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/0470c61c-63b1-49bd-8662-2f9eac0e120b) | ![Windows Defender - Run Anyway screen](https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/d07dc9b6-cdc3-48e1-8111-7062341b662d) |
- For macOS, click on **OK** and run `sudo spctl --master-disable` in your terminal to allow running unsigned apps. Try running Drifty again. </br>
  <img width="250" alt="gatekeeper" src="https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/2fb88e8a-5de2-4847-98e2-9e4972d9486d">

## Contributing to the project 🤝

- We welcome 🤝 your contributions to this Open-Source project.
- Please check the [**`Contributing Guidelines`**](https://github.com/SaptarshiSarkar12/Drifty/blob/master/CONTRIBUTING.md) and the [**`Roadmaps`**](https://github.com/users/SaptarshiSarkar12/projects/3) for ways to get involved.
- **You can also open an [`issue`](https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose)** to report a bug 🐛, request a feature 💡, or suggest an improvement 📈.
- Please follow this project's [**`Code Of Conduct`**](https://github.com/SaptarshiSarkar12/Drifty/blob/master/CODE_OF_CONDUCT.md) to maintain a respectful and inclusive environment.
- Please note that this project uses [**`Semantic Versioning`**](https://semver.org/) for its releases.
- Join our [**`Discord Server`**](https://discord.gg/DeT4jXPfkG) to stay updated on this project and chat 💬 with the maintainer and the other contributors.

## Stats 📊

![Repo analysis](https://repobeats.axiom.co/api/embed/9b39d68e1ca7e9523e4454b352930d61109915a1.svg)

## Contact Us 📞

If you have any questions or need further assistance,
please visit our [Contact Page](https://drifty.vercel.app/contact).

![Contact Us](https://github.com/user-attachments/assets/8ed584c7-91ef-45da-8486-a9854fe7b7b7)

## Support 🙏

Please **leave a star ⭐** on this project to _support us_.

## Thanks to all the contributors ❤️

[![Contributors' gallery at a glance](https://contrib.rocks/image?repo=SaptarshiSarkar12/Drifty&max=200)](https://github.com/SaptarshiSarkar12/Drifty/graphs/contributors)
