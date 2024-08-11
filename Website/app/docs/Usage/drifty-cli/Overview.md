# Drifty CLI Overview

Drifty CLI is the command-line interface for Drifty that allows you to download files from the internet with just a few commands. You can integrate Drifty CLI into your scripts and automate the download process. So, it is important to understand the different commands and options available in Drifty CLI.

> [!NOTE]
> We have assumed that the name of the executable for Drifty CLI is `drifty`. Replace `drifty` with the actual executable name if it is different in your case.

![Drifty CLI Help](https://github.com/user-attachments/assets/b695f3b4-c695-4dfe-a431-9742671488f9)

## Commands

Drifty CLI supports the following commands:

### Help

**Command**: `--help` or `-h`

**Description**: Display the help message with all the available commands and options.

**Usage**:

```bash
drifty --help
```

### Version

**Command**: `--version` or `-v`

**Description**: Display the version of Drifty CLI along with its components.

**Usage**:

```bash
drifty --version
```

### Batch Download

**Command**: `--batch <file>` or `-b <file>`

**Description**: Download multiple files without the need to specify the URL each time. You need to create an YAML file with the list of URLs you want to download. You can also specify the directory and file name for each URL. The YAML file should have the following format:

```yaml
links: ["<URL1>", "<URL2>", "<URL3>", ...]
# Optional: Specify the directory and file name for each URL
fileNames: ["<FileName1>", "<FileName2>", "<FileName3>", ...]
directory: ["<SameDirectoryForAll>"]
directories: ["<Directory1>", "<Directory2>", "<Directory3>", ...]
```

- **links** (**Required**): List of URLs to download.
- **fileNames** (**Optional**): List of file names to save the downloaded files. The order should match the order of URLs. If not specified, the file name will be automatically detected from the URL.
- **directory** or **directories** (**Optional**): Specify the directory where the files will be saved. Use **directory** to save all files in the same directory. Use **directories** to save each file in the corresponding directory specified in the order of URLs. By default, files are saved in the last used directory or the default download directory (`{user_home}/Downloads`) if not specified.

**Example Configuration**:

```yaml
# batch.yaml (You can name the file as you like)
links: ["https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz", "https://www.youtube.com/watch?v=pBy1zgt0XPc"]
fileNames: ["jdk-22.tar.gz", "What is GitHub?.mp4"]
directories: ["/home/user/Downloads", "/home/user/Videos"]
```

**Usage**:

```bash
drifty --batch batch.yaml
```

### Add links to Queue

**Command**: `--add <URL1> <URL2> ...`

**Description**: Add one or more URLs to the download queue. The URLs will be downloaded sequentially.

**Usage**:

```bash
drifty --add "https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz" "https://www.youtube.com/watch?v=pBy1zgt0XPc"
```

### List links in Queue

**Command**: `--list`

**Description**: List all the URLs in the download queue.

**Usage**:

```bash
drifty --list
```

### Remove links from Queue

**Command**: `--remove <index1> <index2> ...` or `--remove all`

**Description**: Remove one or more URLs from the download queue. You can specify the index of the URLs to remove, or use `all` to clear the entire queue.

**Usage**:

```bash
drifty --remove 1 3
```

### Download from Queue

**Command**: `--get`

**Description**: Start downloading the URLs in the download queue. The URLs will be downloaded sequentially.

**Usage**:

```bash
drifty --get
```

### Destination Download Folder

> [!NOTE]
> This command is not a standalone command. It must be used with the URL of the file you want to download.
> You can also use the `--name` command in conjunction with this command.

**Command**: `--location <path>` or `-l <path>`

**Description**: Specify a custom download location for the files. If not specified, you will be prompted to choose the download location each time you download a file (Default download location, Last-used directory, or Custom directory).

**Usage**:

```bash
drifty https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz --location /home/user/Downloads
```

### File Name

> [!NOTE]
> This command is not a standalone command. It must be used with the URL of the file you want to download.
> You can also use the `--location` command in conjunction with this command.

**Command**: `--name <filename>` or `-n <filename>`

**Description**: Specify a custom file name, including the extension if applicable. If not provided, the file name will be automatically detected from the URL.

**Usage**:

```bash
drifty https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz --name jdk-22.tar.gz
```