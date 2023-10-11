<a href="https://saptarshisarkar12.github.io/Drifty/">
    <p align="center">
        <img src="https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/92b11509-2115-4f80-8188-19821b258332" alt="Drifty Banner with App Icon">
    </p>
</a>


<p align="center">
    <a href="https://github.com/SaptarshiSarkar12/Drifty/releases/latest/"><img src="https://img.shields.io/github/v/release/SaptarshiSarkar12/Drifty?          color=%23FFFF0g&amp;label=Drifty" alt="Release Version"></a>
    <a href="https://github.com/SaptarshiSarkar12/Drifty/blob/master/LICENSE"><img src="https://img.shields.io/github/license/SaptarshiSarkar12/Drifty" alt="License"></a>
    <a href="https://github.com/SaptarshiSarkar12/Drifty/releases/latest/"><img src="https://img.shields.io/github/downloads/SaptarshiSarkar12/Drifty/total" alt="Total No. Of Downloads of Drifty"></a>    
</p>

<p align="center">
    <a href="https://github.com/SaptarshiSarkar12/Drifty/actions/workflows/build.yml"><img src="https://github.com/SaptarshiSarkar12/Drifty/actions/workflows/build.yml/badge.svg" alt="Build Workflow Status"/></a> 
    <a href="https://saptarshisarkar12.github.io/Drifty/"><img src="https://img.shields.io/github/deployments/SaptarshiSarkar12/Drifty/github-pages?label=Website Deployment" alt="GitHub Pages Deployment Status"></a>
</p>

<p align="center">
    <a href="https://github.com/SaptarshiSarkar12/Drifty/stargazers"><img src="https://img.shields.io/github/stars/SaptarshiSarkar12/Drifty?    label=Leave%20a%20star&amp;style=social" alt="GitHub Stargazers for Drifty"></a> 
    <a href="https://twitter.com/SSarkar2007"><img src="https://img.shields.io/twitter/follow/SSarkar2007?style=social" alt="Follow us on Twitter"></a> 
    <a href="https://discord.gg/DeT4jXPfkG"><img src="https://img.shields.io/discord/1034035416300519454?label=Discord&amp;logo=discord" alt="Discord Server"></a>
</p>

## About 🔥

- [**Drifty**](https://github.com/SaptarshiSarkar12/Drifty/) is an **open-source** interactive **File Downloader system** built using _Java_. 
- It is both available in **Command Line Interface** (_CLI_) and **Graphical User Interface** (_GUI_) mode.

## Demo
See the video below to know how to use the application. For any help, you can open an [`issue`](https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose/).

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
### Drifty GUI

https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/aa7ea548-f312-4345-a053-d3ee13e67c8e

### Drifty CLI

https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/8a32ca07-1922-4c21-895d-44164bec9d76

## Tech Stack

The _Application_ **Drifty** is built using the following technologies :
- [Java](https://www.oracle.com/java/#rc30p1) - A programming language for building machine independent applications.

The [_Website_](https://saptarshisarkar12.github.io/Drifty/) of **Drifty** is built using the following technologies :
- [Next.js](https://nextjs.org/) - A JavaScript framework for building server-rendered React applications.
- [Tailwind CSS](https://tailwindcss.com/) - A utility-first CSS framework.

## Quickstart 🚀🚀
For **Users** :
### Using Drifty Application executable
1. Open the [`website of Drifty`](https://saptarshisarkar12.github.io/Drifty/) </p>
    ![Homepage of Drifty Website](https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/b3a63e16-7cb7-4ad9-b00e-ed4d4d690bd3)
2. Click on [`Download`](https://saptarshisarkar12.github.io/Drifty/#download) </p>
    ![Download Button in Drifty Website pointed out](https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/7b850e1c-a034-4085-ae25-9e5090891faa)
4. At first, select the **Application Type** from the Dropdown menu (_By default, it is GUI_). For downloading _Drifty CLI_, select **CLI** and for _Drifty GUI_, select **GUI** from the dropdown menu. Click on the **Download Now** button based on your Operating System (_Windows, Linux, MacOS_). For **_Windows_** user, a MSI file is available for **Drifty GUI**. You can click on the **_Prefer the msi?_** text and it will download the latest MSI file for you 🎉!.</p>
    ![Download Webpage of Drifty](https://github.com/SaptarshiSarkar12/Drifty/assets/105960032/f8eb6151-ac6c-4383-a5d7-d03718f64cab)
5. Go to your Downloads folder and run the file you downloaded.
   > [!NOTE]   
   > Follow the on-screen instructions (For PKG and MSI files) for setting up Drifty GUI.
6. See [`Demo`](https://saptarshisarkar12.github.io/Drifty/#demo) for demonstrations on how to use the application.
7. Leave a [`star`](https://github.com/SaptarshiSarkar12/Drifty/stargazers) on GitHub if you liked the project.
8. For any issues or feature requests, you may discuss that on our [`Discord Server`](https://discord.gg/DeT4jXPfkG).

### Using Drifty Application via Docker 🐋

> [!IMPORTANT]   
> - To download files in your local directory, you need to pass that directory as a volume to Drifty.   
> Suppose, you want to download the file in your `home/username/Downloads` directory, then, the volume flag that you need to pass to docker will be `-v /home/username/Downloads:/root/Downloads`.   
> - You can pass `-v /home/username/.drifty:/root/.config` to enable the docker container to store the Drifty's data in your local directory, making initialization of Drifty much faster 🚀.
> - For **Linux**, it is advisable to use **Docker Engine** directly instead of running images using **Docker Desktop**. You can do the same by adding **_`sudo`_** before the docker commands.

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
    docker run -e DISPLAY=$DISPLAY --net=host ghcr.io/saptarshisarkar12/drifty-gui:master
    ```
    > [!IMPORTANT]  
    > For **Linux and Windows**, please run `xhost +local:docker` before running the GUI docker image.   
    > For **macOS**, please follow [this article](https://cntnr.io/running-guis-with-docker-on-mac-os-x-a14df6a76efc) to run GUI docker images.
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

### Using Docker 🐋 for Development
1. Run `docker compose run gui` to start **Drifty GUI** and `docker compose run cli` to start **Drifty CLI**. The CLI and GUI will be built using your changed source code.
   > [!IMPORTANT]  
   > For **Linux and Windows**, please run `xhost +local:docker` before running the GUI docker image.   
   > For **macOS**, please follow [this article](https://cntnr.io/running-guis-with-docker-on-mac-os-x-a14df6a76efc) to run GUI docker images.
2. Drifty app will appear on the screen and you can test it 🎉!

## Safety warning ⚠ while Downloading ⬇

![image](https://user-images.githubusercontent.com/58129377/193471489-87ee10a0-f719-47ef-9d46-e5b71c611d4b.png)

It is because Microsoft Defender could not verify it is safe as it is not commonly downloaded. Feel free to proceed with the following steps:
- Click on the three dots. 
- Click on keep.

![image](https://user-images.githubusercontent.com/58129377/193471652-d88981c3-d903-406f-bc06-53cf77db9bf6.png)

- Click **`Show More`**.
- Click **`Keep anyway`**.

![image](https://user-images.githubusercontent.com/58129377/193471671-e047003c-95e7-43ed-8d37-d3f401b36164.png)

- Now its downloaded, you can proceed.🎉

### Report the file as safe ✔️

You can also provide your feedback to Microsoft Defender by reporting it to be Safe, by following following steps:
- Click on three dots.
- Click on **`Report this file as safe`** instead of clicking on **`Keep`**.

![image](https://user-images.githubusercontent.com/58129377/193471838-63ba50ba-e303-48b4-b7c1-b71e6c0663e7.png)

- It will take you to **`Report a download`** page. You can provide your feedback there.😄

## Contributing to the project
- Please feel free to contribute to this open-source project. 
- See [**`Contribuing Guidelines`**](https://github.com/SaptarshiSarkar12/Drifty/blob/master/CONTRIBUTING.md) and [**`Projects`**](https://github.com/users/SaptarshiSarkar12/projects/3) for ways to contribute. 
- **Feel free to open an [`issue`](https://github.com/SaptarshiSarkar12/Drifty/issues/new/choose)**. 
- Please adhere to this project's [**`Code Of Conduct`**](https://github.com/SaptarshiSarkar12/Drifty/blob/master/CODE_OF_CONDUCT.md). 
- Remember, this project follows [**`Semantic Versioning`**](https://semver.org/) for the releases. 
- Join our [**`Discord Server`**](https://discord.gg/DeT4jXPfkG) to get updates on this project and discuss verbosely on the changes that you want to make.

## Stats 📊
![Repo analysis](https://repobeats.axiom.co/api/embed/9b39d68e1ca7e9523e4454b352930d61109915a1.svg)

## Support 🙏
Please **leave a star ⭐** on this project to _support us_.

## Thanks to all the contributors ❤️

[![Contributors' gallery at a glance](https://contrib.rocks/image?repo=SaptarshiSarkar12/Drifty&max=200)](https://github.com/SaptarshiSarkar12/Drifty/graphs/contributors)