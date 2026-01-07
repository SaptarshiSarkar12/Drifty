package main;

import gui.init.GUITestEnvironment;
import gui.preferences.AppSettings;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import properties.Program;
import support.Job;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DriftyGUITest extends GUITestEnvironment {
    @TempDir
    private static Path tempDir;
    private static boolean oldJobsFileRenamed = false;
    private static final String ORIGINAL_LAST_FOLDER = AppSettings.GET.lastDownloadFolder();

    @BeforeAll
    public static void setup() {
        try {
            // Rename existing JOBS file if it exists to avoid interference with tests
            String jobFilePath = Program.get(Program.JOB_FILE);
            File jobFile = new File(jobFilePath);
            File newJobFile = new File(jobFilePath + ".bak");
            if (jobFile.exists()) {
                boolean isRenamed = jobFile.renameTo(newJobFile);
                if (!isRenamed) {
                    Assertions.fail("Failed to rename existing JOBS file for testing.");
                }
                oldJobsFileRenamed = true;
            }
        } catch (NullPointerException ignored) {
        } // Ignore if jobs file does not exist
    }

    @Test
    @DisplayName("Test Link Detection Functionality")
    @Order(1)
    public void testLinkDetection(FxRobot robot) {
        String link = "https://www.youtube.com/watch?v=4jqNr1bZvMc";
        robot.write(link);
        TextField linkField = robot.lookup("#tfLink").queryAs(TextField.class);
        while (linkField.isDisable() || linkField.getText().equals(link)) {
            // Wait until the link field is enabled which indicates processing is done
            robot.sleep(1000);
        }
        // Add assertions to verify that the link was processed correctly
        ListView<Job> listView = (ListView<Job>) robot.lookup("#jobListView").queryAs(ListView.class);
        Assertions.assertNotNull(listView);
        ObservableList<Job> jobs = listView.getItems();
        Assertions.assertFalse(jobs.isEmpty(), "The job list should not be empty after adding a link.");
        boolean linkFound = jobs.stream().anyMatch(job -> job.matchesLink(link));
        Assertions.assertTrue(linkFound, "The job list should contain the added link.");
    }

    @Test
    @DisplayName("Test filepath Update on Directory Change")
    @Order(2)
    public void testFilepathUpdate(FxRobot robot) {
        ListView<Job> listView = (ListView<Job>) robot.lookup("#jobListView").queryAs(ListView.class);
        Job job = listView.getItems().getFirst();
        robot.interact(() -> listView.getSelectionModel().select(job));
        Set<Node> cells = robot.lookup(".list-cell").queryAll();
        Platform.runLater(() -> listView.scrollTo(job));
        for (Node cell : cells) {
            ListCell<?> listCell = (ListCell<?>) cell;
            if (listCell.getItem() == job) {
                robot.interact(() -> cell.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, true, false, false, null)));
                break;
            }
        }
        String newDir = tempDir.toAbsolutePath().toString();
        TextField dirField = robot.lookup("#tfDir").queryAs(TextField.class);
        robot.clickOn(dirField).eraseText(dirField.getText().length()).write(newDir).clickOn("#btnSave");
        robot.sleep(2000); // Wait for the UI to update
        ListView<Job> updatedListView = (ListView<Job>) robot.lookup("#jobListView").queryAs(ListView.class);
        Job updatedJob = updatedListView.getItems().getFirst();
        Assertions.assertEquals(newDir, updatedJob.getDir(), "The job directory should be updated to the new value.");
    }

    @Test
    @DisplayName("Test File Download Start Functionality")
    @Order(3)
    public void testFileDownloadStart(FxRobot robot) {
        ListView<Job> listView = (ListView<Job>) robot.lookup("#jobListView").queryAs(ListView.class);
        robot.clickOn("#btnStart");
        while (!listView.getItems().isEmpty()) {
            robot.sleep(1000); // Wait until all jobs are processed
        }
        Assertions.assertTrue(listView.getItems().isEmpty(), "The job list should be empty after starting downloads.");
        try (var files = Files.list(tempDir)) {
            boolean hasFiles = files.findAny().isPresent();
            Assertions.assertTrue(hasFiles, "Downloaded files should exist in the specified directory.");
        } catch (IOException e) {
            Assertions.fail("Exception occurred while verifying downloaded files: " + e.getMessage());
        }
        robot.sleep(3000); // Additional wait to ensure processing is complete
    }

    @AfterAll
    public static void cleanup() {
        String jobFilePath = Program.get(Program.JOB_FILE);
        if (oldJobsFileRenamed) {
            // Restore original JOBS file if it was renamed
            if (jobFilePath != null) {
                File jobFile = new File(jobFilePath);
                File newJobFile = new File(jobFilePath + ".bak");
                if (newJobFile.exists()) {
                    if (jobFile.exists()) {
                        boolean isDeleted = jobFile.delete();
                        if (!isDeleted) {
                            Assertions.fail("Failed to delete test JOBS file during cleanup.");
                        }
                    }
                    boolean isRenamedBack = newJobFile.renameTo(jobFile);
                    if (!isRenamedBack) {
                        Assertions.fail("Failed to restore original JOBS file during cleanup.");
                    }
                }
            }
        } else {
            // Delete the test JOBS file if it was created during tests
            if (jobFilePath != null) {
                File jobFile = new File(jobFilePath);
                if (jobFile.exists()) {
                    boolean isDeleted = jobFile.delete();
                    if (!isDeleted) {
                        Assertions.fail("Failed to delete test JOBS file during cleanup.");
                    }
                }
            }
        }
        AppSettings.SET.lastFolder(ORIGINAL_LAST_FOLDER);
    }
}
