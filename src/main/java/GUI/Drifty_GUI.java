package GUI;

import Backend.Drifty;
import Backend.ProgressBarThread;
import Utils.CreateLogs;
import Utils.DriftyConstants;
import Utils.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * This class deals with the Graphical User Interface (GUI) version of Drifty.
 * @since 2.0.0
 * @version 2.0.0
 */
public class Drifty_GUI {
    static JFrame window;
    static int windowWidth;
    static boolean isDownloadButtonPressed;
    /**
     * Boolean value to know if a file is being downloaded or not.
     */
    static boolean isFileBeingDownloaded = false;
    static JPanel input;
    static JPanel downloadLayout;
    static JLabel linkOutputText;
    static JLabel directoryOutputText;
    static JLabel fileNameOutputText;
    static JButton downloadButton;
    static int windowHeight;
    static JTextField linkInputText;
    static JTextField directoryInputText;
    static JTextField fileNameInputText;
    static JLabel downloadOutputText;
    static String linkToFile;
    static String directoryForDownloading;
    static String fileName;
    static float downloadProgress;
    static JProgressBar downloadProgressBar;
    static boolean isYouTubeURL;
    static CreateLogs logger = CreateLogs.getInstance();
    public static void main(String[] args) {
        logger.log(DriftyConstants.LOGGER_INFO, DriftyConstants.GUI_APPLICATION_STARTED); // log a message when the Graphical User Interface (GUI) version of Drifty is triggered to start
        initializeScreen(); // Initializing the screen
        initializeIOFields(); // Initializing the Input and Output fields
        setDefaultInputs(); // Setting default values for input fields
        startInstantInputValidating(); // Starting the tasks to instantly validate the inputs and show an output message accordingly
        window.pack();
        window.setVisible(true);
    }

    /**
     * This method sets the value of the boolean <b>IsFileBeingDownloaded</b>.
     * @param value If the file is being downloaded, <code>true</code> should be passed else <code>false</code>.
     */
    public static void setIsFileBeingDownloaded(boolean value) {
        isFileBeingDownloaded = value;
    }

    private static void initializeIOFields() {
        JPanel inputPanel = new JPanel();
        GridLayout layout = new GridLayout(5, 1, 0, 1);
        layout.layoutContainer(inputPanel);
        inputPanel.setLayout(layout);

        JPanel linkLayout = new JPanel();
        linkLayout.setBackground(Color.white);
        linkLayout.setLayout(new GridLayout(2, 1, 0, 1));
        JPanel linkInput = new JPanel();
        linkInput.setBackground(Color.white);
        linkInput.setLayout(new FlowLayout());
        JLabel linkText = new JLabel("Link : ");
        linkText.setFont(new Font("Arial", Font.BOLD, 23));
        linkInputText = new JTextField(); // link input area
        linkInputText.setHorizontalAlignment(SwingConstants.CENTER);
        linkInputText.setColumns(60);
        linkOutputText = new JLabel(); // Link Output
        linkOutputText.setHorizontalAlignment(SwingConstants.CENTER);
        linkInput.add(linkText);
        linkInput.add(linkInputText);
        linkLayout.add(linkInput);
        linkLayout.add(linkOutputText);
        inputPanel.add(linkLayout);

        JPanel directoryLayout = new JPanel();
        directoryLayout.setBackground(Color.white);
        directoryLayout.setLayout(new GridLayout(2, 1, 0, 1));
        JPanel directoryInput = new JPanel();
        directoryInput.setBackground(Color.white);
        directoryInput.setLayout(new FlowLayout());
        JLabel directoryText = new JLabel("Directory : ");
        directoryText.setFont(new Font("Arial", Font.BOLD, 23));
        directoryInputText = new JTextField(); // directory input area
        directoryInputText.setHorizontalAlignment(SwingConstants.CENTER);
        directoryInputText.setColumns(50);
        directoryOutputText = new JLabel(); // directory Output
        directoryOutputText.setHorizontalAlignment(SwingConstants.CENTER);
        directoryInput.add(directoryText);
        directoryInput.add(directoryInputText);
        directoryLayout.add(directoryInput);
        directoryLayout.add(directoryOutputText);
        inputPanel.add(directoryLayout);

        JPanel fileNameLayout = new JPanel();
        fileNameLayout.setBackground(Color.white);
        fileNameLayout.setLayout(new GridLayout(2, 1, 0, 1));
        JPanel fileNameInput = new JPanel();
        fileNameInput.setBackground(Color.white);
        fileNameInput.setLayout(new FlowLayout());
        JLabel fileNameText = new JLabel("File Name (with extension) : ");
        fileNameText.setFont(new Font("Arial", Font.BOLD, 23));
        fileNameInputText = new JTextField(); // file name input area
        fileNameInputText.setHorizontalAlignment(SwingConstants.CENTER);
        fileNameInputText.setColumns(30);
        fileNameOutputText = new JLabel(); // Link Output
        fileNameOutputText.setHorizontalAlignment(SwingConstants.CENTER);
        fileNameInput.add(fileNameText);
        fileNameInput.add(fileNameInputText);
        fileNameLayout.add(fileNameInput);
        fileNameLayout.add(fileNameOutputText);
        inputPanel.add(fileNameLayout);

        downloadLayout = new JPanel();
        downloadLayout.setBackground(Color.white);
        downloadLayout.setLayout(new GridLayout(2, 1));
        JPanel downloadButtonLayout = new JPanel();
        downloadButtonLayout.setLayout(new FlowLayout());
        downloadButtonLayout.setBackground(Color.white);
        downloadButton = new JButton("Download");
        downloadButton.setFont(new Font("Helvetica Bold", Font.BOLD, 15));
        downloadOutputText = new JLabel();
        downloadOutputText.setHorizontalAlignment(SwingConstants.CENTER);
        downloadLayout.add(downloadOutputText);
        downloadButtonLayout.add(downloadButton);
        downloadLayout.add(downloadButtonLayout);
        JPanel downloadProgressBarLayout = new JPanel();
        downloadProgressBarLayout.setBackground(Color.white);
        downloadProgressBarLayout.setLayout(new FlowLayout());
        downloadProgressBar = new JProgressBar(0, 100);
        downloadProgressBar.setPreferredSize(new Dimension(500, 20));
        downloadProgressBar.setVisible(false);
        downloadProgressBarLayout.add(downloadProgressBar);
        inputPanel.add(downloadLayout);
        inputPanel.add(downloadProgressBarLayout);

        ActionListener download = e -> {
            disableInputs();
            stopInstantInputValidating();
            saveInputs();

            download();

            enableInputs();
            setDefaultInputs();
            startInstantInputValidating();
        };
        downloadButton.addActionListener(download);

        input.add(inputPanel);
    }

    public static synchronized void getYouTubeVideoDownloadProgress() {
        Scanner sc = new Scanner("./output.txt");
        while (isFileBeingDownloaded) {
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
        }
    }

    /**
     * This method <b>sets the download progress</b> in the GUI Application.
     */
    private static void setDownloadProgress() {
        SwingWorker<Void, Void> getProgress = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                while (isFileBeingDownloaded) {
                    downloadProgress = ProgressBarThread.getTotalDownloadPercent();
                }
                return null;
            }
        };
        getProgress.execute();
        SwingWorker<Void, Void> setProgress = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                while (isFileBeingDownloaded) {
                    downloadProgressBar.setValue((int) (downloadProgress));
                }
                return null;
            }
        };
        setProgress.execute();
    }

    /**
     * This method deals with <b>initializing and calling the Backend</b> to <i>download</i> the file.
     * @since 2.0.0
     */
    private static void download() {
        Drifty backend = new Drifty(linkToFile, directoryForDownloading, fileName, linkOutputText, directoryOutputText, downloadOutputText, fileNameOutputText);
        downloadButton.setVisible(false);
        if (isYouTubeURL){
            /*
            Does not work !
             */
//            try {
//                System.out.println("Creating file...");
//                Files.createFile(Path.of("./output.txt"));
//                System.setOut(new PrintStream("./output.txt"));
//            } catch (IOException e) {
//                System.out.println("Failed to create file!");
//            }
//            Task<Void> getYtDlpProgress = new Task<>() {
//                @Override
//                protected Void call() {
//                    getYouTubeVideoDownloadProgress();
//                    return null;
//                }
//            };
//            new Thread(getYtDlpProgress).start();
        } else {
            SwingWorker<Void, Void> setProgress = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    setDownloadProgress();
                    return null;
                }
            };
            setProgress.execute();
        }
        SwingWorker<Void, Void> startDownload = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                backend.start();
                return null;
            }
        };
        startDownload.execute();
        SwingWorker<Void, Void> recreateDownloadSetup = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                while (true){
                    if (!isFileBeingDownloaded){
                        downloadProgressBar.setVisible(false);
                        downloadButton.setVisible(true);
                        break;
                    }
                }
                return null;
            }
        };
        recreateDownloadSetup.execute();
    }

    /**
     * This method <b>receives the input</b> from the input fields and stores it in variables.
     * @since 2.0.0
     */
    private static void saveInputs() {
        linkToFile = linkInputText.getText();
        if (Utility.isYoutubeLink(linkToFile)){
            isYouTubeURL = true;
        }
        if (directoryForDownloading == null){
            directoryForDownloading = directoryInputText.getText();
        }
        fileName = fileNameInputText.getText();
    }

    /**
     * This method <b>stops</b> the instant input validator task from validating the inputs as the user types them in the respective input fields.
     */
    private static void stopInstantInputValidating(){
        isFileBeingDownloaded = true;
    }

    /**
     * This method <b>disables</b> the input fields in the Graphical User Interface (GUI) version of Drifty, to be editable.
     * @since 2.0.0
     */
    private static void disableInputs() {
        isDownloadButtonPressed = true;
        linkInputText.setEditable(false);
        directoryInputText.setEditable(false);
        fileNameInputText.setEditable(false);
    }

    /**
     * This method <b>enables</b> the input fields in the Graphical User Interface (GUI) version of Drifty, to be editable.
     * @since 2.0.0
     */
    private static void enableInputs() {
        linkInputText.setEditable(true);
        directoryInputText.setEditable(true);
        fileNameInputText.setEditable(true);
        isDownloadButtonPressed = false;
    }

    /**
     * This method <b>sets the default values for the input fields</b> in the Graphical User Interface (GUI) version of Drifty.
     * @since 2.0.0
     */
    private static void setDefaultInputs() {
        SwingWorker<Void, Void> setDefaultDirectory = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String defaultDirectory = Utility.saveToDefault();
                directoryInputText.setText(defaultDirectory);
                return null;
            }
        };
        setDefaultDirectory.execute();
        SwingWorker<Void, Void> setDefaultFilename = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String previous_url = "";
                while (!isFileBeingDownloaded) {
                    String url = linkInputText.getText();
                    if (!url.equals(previous_url)) {
                        if (!Utility.isYoutubeLink(url)) {
                            String fileName = Utility.findFilenameInLink(url);
                            if (fileName != null) {
                                fileNameInputText.setText(fileName);
                            } else {
                                fileNameInputText.setText("");
                            }
                        }
                        previous_url = url;
                    }
                }
                return null;
            }
        };
        setDefaultFilename.execute();
    }

    /**
     * This method initializes the <b>main screen of the main Window</b> (default) with properties such as <b>window title</b>, <b>position on screen</b>, etc.
     */
    private static void initializeScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g.: java.awt.Dimension[width=1366,height=768]
        windowHeight = (int) screenSize.getHeight(); // E.g.: 768
        windowWidth = (int) screenSize.getWidth(); // E.g.: 1366
        window = new JFrame("Drifty (GUI)");
        window.setSize(windowWidth, windowHeight);
        window.setPreferredSize(screenSize);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.white);

        JMenu menu = new JMenu("Menu");
        JMenuItem website = new JMenuItem("Project Website");
        website.setBackground(Color.white);
        JMenuItem about = new JMenuItem("About");
        about.setBackground(Color.white);
        JMenuItem exit = new JMenuItem("Exit");
        exit.setBackground(Color.white);
        menu.add(website);
        menu.add(about);
        menu.add(exit);

        JMenu help = new JMenu("Help");
        JMenuItem contactUs = new JMenuItem("Contact Us");
        contactUs.setBackground(Color.white);
        JMenuItem contribute = new JMenuItem("Contribute");
        contribute.setBackground(Color.white);
        JMenuItem bug = new JMenuItem("Report a Bug");
        bug.setBackground(Color.white);
        JMenuItem securityVulnerability = new JMenuItem("Report a Security Vulnerability");
        securityVulnerability.setBackground(Color.white);
        JMenuItem feature = new JMenuItem("Suggest a Feature");
        feature.setBackground(Color.white);
        help.add(contactUs);
        help.add(contribute);
        help.add(bug);
        help.add(securityVulnerability);
        help.add(feature);

        menuBar.add(menu);
        menuBar.add(help);
        menuBar.setVisible(true);
        window.setJMenuBar(menuBar);

        website.addActionListener(e -> openWebsite(Drifty.projectWebsite, "project website"));
        exit.addActionListener(e -> {
            logger.log(DriftyConstants.LOGGER_INFO, DriftyConstants.GUI_APPLICATION_TERMINATED);
            System.exit(0);
        });
        contactUs.addActionListener(e -> openWebsite("https://saptarshisarkar12.github.io/Drifty/contact.html", "contact us webpage"));
        contribute.addActionListener(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty", "repository website for contribution"));
        bug.addActionListener(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=bug%2CApp&template=Bug-for-application.yaml&title=%5BBUG%5D+", "issue webpage to file a Bug"));
        securityVulnerability.addActionListener(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/security/advisories/new", "Security Vulnerability webpage"));
        feature.addActionListener(e -> openWebsite("https://github.com/SaptarshiSarkar12/Drifty/issues/new?assignees=&labels=enhancement%2CApp&template=feature-request-application.yaml&title=%5BFEATURE%5D+", "issue webpage to suggest feature"));

        input = new JPanel();
        input.setBackground(Color.white);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignored) {}
        GridLayout layout = new GridLayout(2, 1);
        input.setLayout(layout);
        ImageIcon driftyBanner = new ImageIcon("./Drifty Banner.png");
        JLabel drifty = new JLabel(driftyBanner);
        drifty.setHorizontalAlignment(SwingConstants.CENTER);
        drifty.setVerticalAlignment(SwingConstants.TOP);
        drifty.setVisible(true);
        input.add(drifty);
        window.add(input);
        window.setContentPane(input);
    }

    /**
     * This method <b>starts</b> the instant input validating task from validating the inputs as the user types them in the respective input fields.
     * @since 2.0.0
     */
    private static void startInstantInputValidating() {
        SwingWorker<Void, Void> validateLink = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String previous_url = "";
                while (!isFileBeingDownloaded) {
                    String url = linkInputText.getText();

                    if (!url.equals(previous_url)) { // checks whether the link is edited or not (helps in optimising and improves performance)
                        linkOutputText.setForeground(Color.green);
                        linkOutputText.setText("Validating link ...");
                        if (url.contains(" ")) {
                            linkOutputText.setForeground(Color.RED);
                            linkOutputText.setText("Link should not contain whitespace characters!");
                            downloadButton.setEnabled(false);
                        } else if (url.length() == 0) {
                            linkOutputText.setForeground(Color.RED);
                            linkOutputText.setText("Link cannot be empty!");
                            downloadButton.setEnabled(false);
                        } else {
                            try {
                                Utility.isURLValid(url);
                                linkOutputText.setForeground(Color.GREEN);
                                linkOutputText.setText("Link is valid!");
                                downloadButton.setEnabled(true);
                            } catch (Exception e) {
                                linkOutputText.setForeground(Color.RED);
                                linkOutputText.setText(e.getMessage());
                                downloadButton.setEnabled(false);
                            }
                        }
                        previous_url = url;
                    }
                }
                return null;
            }
        };
        validateLink.execute();
        SwingWorker<Void, Void> validateDirectory = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String previous_directory = "";
                while (!isFileBeingDownloaded) {
                    String directory = directoryInputText.getText();
                    if (!directory.equals(previous_directory)){
                        directoryOutputText.setForeground(Color.RED);
                        if (directory.length() == 0){
                            directoryOutputText.setText("Directory cannot be empty!");
                            downloadButton.setEnabled(false);
                        } else {
                            File file = new File(directory);
                            if (file.exists() && file.isDirectory()) {
                                directoryOutputText.setForeground(Color.GREEN);
                                directoryOutputText.setText("Directory exists!");
                                downloadButton.setEnabled(true);
                            } else {
                                directoryOutputText.setText("Directory does not exist!");
                                downloadButton.setEnabled(false);
                            }
                        }
                    }
                    previous_directory = directory;
                }
                return null;
            }
        };
        validateDirectory.execute();
    }

    /**
     * This method <b>opens the website link (provided as parameter) in the default web browser</b>.
     * @param websiteURL This is the <b>String representation</b> of the <i>website link</i> to open. E.g.: <a href="https://saptarshisarkar12.github.io/Drifty">"https://saptarshisarkar12.github.io/Drifty"</a>, <a href="https://github.com/SaptarshiSarkar12/Drifty">"https://github.com/SaptarshiSarkar12/Drifty"</a>, etc.
     * @param websiteType This is <b>type of the website</b> to be opened. E.g.: "<b>Project Website</b>", "<b>Contact Us Webpage</b>", etc.
     */
    private static void openWebsite(String websiteURL, String websiteType){
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nux") || osName.contains("nix")){ // for Linux / Unix systems
            try {
                String[] commandsToOpenWebsite = {"xdg-open", websiteURL};
                Runtime openWebsite = Runtime.getRuntime();
                openWebsite.exec(commandsToOpenWebsite);
            } catch (IOException e) {
                logger.log(DriftyConstants.LOGGER_ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        } else if (osName.contains("win") || osName.contains("mac")) { // For macOS and Windows systems
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(websiteURL));
            } catch (IOException | URISyntaxException e) {
                logger.log(DriftyConstants.LOGGER_ERROR, "Cannot open " + websiteType + " - " + e.getMessage());
            }
        }
    }
}
