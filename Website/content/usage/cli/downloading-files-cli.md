# How to download files using Drifty CLI?

Drifty CLI allows you to download files from the internet using a simple command-line interface. There are three ways to download files using Drifty CLI:

1. **Single File Download**: Download individual files by providing their URLs one at a time.
2. **Batch download**: You can download multiple files by providing a YAML file with the list of URLs.
3. **Using Drifty CLI Queue**: Add multiple URLs to a download queue, allowing you to download them at your convenience.

## Single file download

1. Open Drifty CLI in your terminal.
2. You will be prompted to choose the download option. Enter `1` for **Single File download**.
   ![Download Options Prompt](https://github.com/user-attachments/assets/c1bb0da0-e36c-4229-9e8a-a473a973beaa)
3. Enter the URL of the video you want to download.
4. After validating the URL, Drifty will prompt you to choose a download destination folder. Enter

   - `.` if you want to download the video in the default download directory (which is `{user_home}/Downloads{:sh}` in most cases).
   - `L` if you want to use the last used directory.
   - the absolute path of the directory where you want to save the video, e.g., `/home/user/Videos{:sh}`.

   ![Download Destination Prompt](https://github.com/user-attachments/assets/1eb90bd7-1ae8-47f3-8a16-e4b8d5c214e6)

5. The file name will be automatically detected from the URL. You will be prompted to confirm the file name. Enter `Y` to proceed or `N` to enter a custom file name (followed by the file extension, if any).
   ![File Name Prompt](https://github.com/user-attachments/assets/5c6b0003-2498-457d-b4b3-cade262a19f9)
6. The file will be downloaded to the specified directory.
7. After the download is complete, you will be prompted to download another file or exit the application. Enter `Q` to exit or any other key to continue downloading files.
   ![Exit Prompt](https://github.com/user-attachments/assets/001a1111-d4af-4517-8545-8a5ae831ac70)

## Batch download

1. Open Drifty CLI in your terminal.
2. You will be prompted to choose the download option. Enter `2` for **Batch download**.
   ![Download Options Prompt](https://github.com/user-attachments/assets/c1bb0da0-e36c-4229-9e8a-a473a973beaa)
3. Enter the path to the YAML file containing the list of URLs you want to download.
   For this example, we are going to use the following data:
   ```yaml
   # batch.yml
   links:
     [
       "https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz",
       "https://www.youtube.com/watch?v=pBy1zgt0XPc",
     ]
   ```
   ![Batch File Prompt](https://github.com/user-attachments/assets/d86c42f1-f8f2-44ba-8c96-69be1a76aede)
4. The batch file will be processed, and if any of those files exist with the same name, or you have previously downloaded any, you will be prompted to skip or re-download them.

   - Enter `Y` to continue downloading the file. A new file name will be generated, and you will be prompted to confirm the file name.
   - Enter `N` to skip downloading the file.

   ![File Exists Prompt](https://github.com/user-attachments/assets/6f475d3a-a316-443a-9b17-739a33155a7f)

5. The files will be downloaded sequentially to the default download directory, as we have not specified a custom download location in our YAML file.
   ![Batch Download Progress](https://github.com/user-attachments/assets/1ba81b88-ddb4-4721-aa5f-9579d4634e99)
6. After the download is complete, you will be prompted to download another file or exit the application. Enter `Q` to exit or any other key to continue downloading files.
   ![Exit Prompt](https://github.com/user-attachments/assets/3e8d7491-bb27-454a-b633-092bc45e0260)

## Using Drifty CLI Queue

1. Open your terminal in the directory containing the Drifty CLI executable.
2. Check if any URLs are already in the download queue by using the `--list{:sh}` command.

   ```bash
   drifty --list
   ```

   ![List Command](https://github.com/user-attachments/assets/bc109432-43ef-461a-bc2e-751ce78e87b4)

3. Add URLs to the download queue using the `--add{:sh}` command.

   ```bash
   drifty --add "https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz" "https://www.youtube.com/watch?v=pBy1zgt0XPc"
   ```

   ![Add Command](https://github.com/user-attachments/assets/5043cfe3-adcf-45a1-aead-33f3dec8a8cd)

4. You can view the URLs just added to the queue by using the `--list{:sh}` command.

   ```bash
   drifty --list
   ```

   ![List Command](https://github.com/user-attachments/assets/b675b9e2-2159-4125-8996-535ba03f6cfe)

5. Start downloading the URLs in the download queue using the `--get{:sh}` command.

   ```bash
   drifty --get
   ```

   ![Get Command](https://github.com/user-attachments/assets/0481a556-fea1-4d4f-9383-8e1d842ee388)

6. The files will be downloaded sequentially to the default download directory. After the download is complete, Drifty CLI will automatically remove the URLs from the queue and exit.

   ![Queue Download Done](https://github.com/user-attachments/assets/6ecea884-9587-4a7a-9a62-8b3052ac3a1d)
