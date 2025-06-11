# How to download files using Drifty CLI?

Drifty CLI allows you to download files from the internet using a simple command-line interface. There are three ways to download files using Drifty CLI:

1. **Single File Download**: Download individual files by providing their URLs one at a time.
2. **Batch download**: You can download multiple files by providing a YAML file with the list of URLs.
3. **Using Drifty CLI Queue**: Add multiple URLs to a download queue, allowing you to download them at your convenience.

## Single file download

1. Open Drifty CLI in your terminal.
2. You will be prompted to choose the download option. Enter `2{:js}` for **Single File download**.
   ![Download Options Prompt](https://github.com/user-attachments/assets/c41e73d3-5181-4d20-b708-4d3c63ded67f)
3. Enter the URL of the video you want to download.
4. After validating the URL, Drifty will prompt you to choose a download destination folder. Enter

   - `.` if you want to download the video in the default download directory (which is `user_home/Downloads{:sh}` in most cases).
   - `L` if you want to use the last used directory.
   - the absolute path of the directory where you want to save the video, e.g., `/home/user/Videos{:sh}`.

   ![Download Destination Prompt](https://github.com/user-attachments/assets/1a09c175-3666-4f9c-a4c4-75309cb5f10a)

5. The file name will be automatically detected from the URL. You will be prompted to confirm the file name. Enter `Y` to proceed or `N` to enter a custom file name (followed by the file extension, if any).
   ![File Name Prompt](https://github.com/user-attachments/assets/9496fff3-1357-4fff-bc81-4617ac8838a7)
6. The file will be downloaded to the specified directory.
7. After the download is complete, you will be prompted to download another file or exit the application. Enter `Q` to exit or any other key to continue downloading files.
   ![Exit Prompt](https://github.com/user-attachments/assets/a20a8d5d-3045-4984-937a-df2d8391e3cd)

## Batch download

1. Open Drifty CLI in your terminal.
2. You will be prompted to choose the download option. Enter `1` for **Batch download**.
   ![Download Options Prompt](https://github.com/user-attachments/assets/c41e73d3-5181-4d20-b708-4d3c63ded67f)
3. Enter the path to the YAML file containing the list of URLs you want to download.
   For this example, we are going to use the following data:
   ```yaml
   # batch.yml
   links:
     [
       "https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz",
       "https://www.youtube.com/watch?v=pBy1zgt0XPc",
     ]
   ```
   ![Batch File Prompt](https://github.com/user-attachments/assets/86d884ff-dd4c-40f1-a6b6-e2f3d98411e0)
4. The batch file will be processed, and if any of those files exist with the same name, or you have previously downloaded any, you will be prompted to skip or re-download them.

   - Enter `Y` to continue downloading the file. A new file name will be generated, and you will be prompted to confirm the file name.
   - Enter `N` to skip downloading the file.

   ![File Exists Prompt](https://github.com/user-attachments/assets/acf9d433-7a0b-409d-9b68-1d6326536467)

5. The files will be downloaded sequentially to the default download directory, as we have not specified a custom download location in our YAML file.
   ![Batch Download Progress](https://github.com/user-attachments/assets/f89b9685-76b6-4fd3-98d6-6e1262ea386e)
6. After the download is complete, you will be prompted to download another file or exit the application. Enter `Q` to exit or any other key to continue downloading files.
   ![Exit Prompt](https://github.com/user-attachments/assets/9a568579-d3e2-4a20-93b4-bab73b2bd635)

## Using Drifty CLI Queue

1. Open your terminal in the directory containing the Drifty CLI executable.
2. Check if any URLs are already in the download queue by using the `--list{:sh}` command.

   ```bash
   drifty --list
   ```

   ![Output of List Command initially](https://github.com/user-attachments/assets/5bc75ce4-d404-4306-9a3a-44f1329611f4)

3. Add URLs to the download queue using the `--add{:sh}` command.

   ```bash
   drifty --add "https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz" "https://www.youtube.com/watch?v=pBy1zgt0XPc"
   ```

   ![Output of Add Command](https://github.com/user-attachments/assets/d461c692-0b0f-4031-96cb-fe127335accd)

4. You can view the URLs just added to the queue by using the `--list{:sh}` command.

   ```bash
   drifty --list
   ```

   ![Output of List Command after adding links](https://github.com/user-attachments/assets/7c5e886a-d1d9-4697-a74d-5c84808f3f89)

5. Start downloading the URLs in the download queue using the `--get{:sh}` command.

   ```bash
   drifty --get
   ```

   ![Output of Get Command](https://github.com/user-attachments/assets/f69cada6-f950-4722-ab63-7ffe6162d850)

6. The files will be downloaded sequentially to the default download directory. After the download is complete, Drifty CLI will automatically remove the URLs from the queue and exit.

   ![Queue Download Completed](https://github.com/user-attachments/assets/e75bb16d-28fe-463d-96cc-363501a66e2b)
