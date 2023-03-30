package GUI;

import Backend.Drifty;
import Utils.CreateLogs;
import Utils.DriftyConstants;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Drifty_GUI_New {
    static JFrame window;
    static JTextField linkInputText;
    static CreateLogs logger = CreateLogs.getInstance();
    public static void main(String[] args) {
        logger.log(DriftyConstants.LOGGER_INFO, DriftyConstants.GUI_APPLICATION_STARTED); // log a message when the Graphical User Interface (GUI) version of Drifty is triggered to start
        initializeScreen(); // Initializing the screen
        initializeIOFields(); // Initializing the Input and Output fields

        window.pack();
        window.setVisible(true);
    }

    private static void initializeIOFields() {
        JPanel link = new JPanel();
        FlowLayout linkLayout = new FlowLayout();
        link.setLayout(linkLayout);
        JLabel linkText = new JLabel("Link : ");
        linkText.setFont(new Font("Arial", Font.BOLD, 23));
        linkInputText = new JTextField(); // link input area
        linkInputText.setColumns(60);
        linkLayout.addLayoutComponent("Link Text", linkText);
        linkLayout.addLayoutComponent("Link TextField", linkInputText);

        link.setVisible(true);
        window.add(link);
    }

    /**
     * This method initializes the <b>main screen of the main Window</b> (default) with properties such as <b>window title</b>, <b>position on screen</b>, etc.
     */
    private static void initializeScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // E.g.: java.awt.Dimension[width=1366,height=768]
        int height = (int) screenSize.getHeight(); // E.g.: 768
        int width = (int) screenSize.getWidth(); // E.g.: 1366
        window = new JFrame("Drifty (GUI)");
        window.setSize(width, height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");
        JMenuItem website = new JMenuItem("Project Website");
        JMenuItem about = new JMenuItem("About");
        JMenuItem exit = new JMenuItem("Exit");
        menu.add(website);
        menu.add(about);
        menu.add(exit);

        JMenu help = new JMenu("Help");
        JMenuItem contactUs = new JMenuItem("Contact Us");
        JMenuItem contribute = new JMenuItem("Contribute");
        JMenuItem bug = new JMenuItem("Report a Bug");
        JMenuItem securityVulnerability = new JMenuItem("Report a Security Vulnerability");
        JMenuItem feature = new JMenuItem("Suggest a Feature");
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

        ImageIcon driftyBanner = new ImageIcon("./Drifty Banner.png");
        JLabel drifty = new JLabel(driftyBanner);
        drifty.setHorizontalAlignment(SwingConstants.CENTER);
        drifty.setVerticalAlignment(SwingConstants.TOP);
        drifty.setVisible(true);

        window.add(drifty);
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
