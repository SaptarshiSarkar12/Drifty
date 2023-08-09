This PR has a lot of changes, so I think it's appropriate to outline what I did and why I did it. 

Please tag me on any changes you would like to make before committing the PR.

First, I created a JavaFX GUI using the Swift GUI as a framework. All of the back end code logic for the screen is the same, except I replaced Swift threads with newer, more effecient thread calls.

I added three packages:

* Enums
* GUI
* Preferences

### Enums
This package has three Enum classes

* <b>Mode</b> - Mode is set to CLI by default, but when the GUI class is launched, it is set to GUI. It offers two methods that are useful in determining the run mode:
	* Mode.isGUI()
	* Mode.isCLI()
* <b>OS</b> - This is a static Enum that is set to GUI when the GUI class is launched. Otherwise is stays default at CLI. It can be used anywhere in the code to find out which mode the program is running in.
* <b>Format</b> - This is used to determine if the chosen file extension from the user is a valid extension recognized by `yt-dlp` and if it is, it provides the `-f` argument accordingly. Currently only used in youtube downloads and only in `GUI` mode.

### Preferences
This package has classes that leverage Javas `Preferences` class, and is used to store information that will persist between re-loads of the program. The following information is preserved between program reloads:

* <b>Folders</b> - As the user adds download folders, they accumulate into a list. When they then run a batch download, the list of folders is searched for the filenames in the batch and if it is found, the user will be asked if they want to re-download those files again.
* <b>AutoPaste</b> - Setting is preserved for both GUI classes
* <b>Batch Jobs</b> - When the user creates a batch, it is preserved between reloads. When there is a batch created from failed downloads, it is also preserved.
Using the Preferences class is preferrable to having an .ini file stored somewhere on the users file system as Preferences handles all of that back end stuff automatically and keeps the information in relevant OS file structure and out of the users way.
* <b>Update Timestamp</b> - Stores the timestamp of the last time `yt-dlp` was updated. See <b>Feature Changes</b> below

### GUI
This package has the classes that are relevant to the different JavaFX screens along with other classes such as data structure classes. Those are in the Support package.

* MainGUI - The main user screen. I maintained the same Menu as was in the Swift code. I added a right click ContextMenu for adding and managing download folders
* BatchGUI - This is used to create batches. Right clicking on the form and chosing `info` will pop up a window that explains how to use the form. The instructions are very thorough.

Some of the controls have ToolTips where hovering the mouse on them provides relevant information.

### Feature Changes
* Program will only attempt to update `yt-dlp` to latest version once every 24 hours. It uses ths timestamp value that is set in Preferences and if the time delta is more than 24 hours, it will run the code that updates the program.
	* <b>HOWEVER</b> - I haven't looked at that code in detail to see if you somehow update the program that is stored in the Resources folder. I am aware that in CLI mode, those programs are extracted to the users hard drive where I'm assuming the updated program is also stored. We might want to consider extracting the program for GUI mode as well, it just seemed rather a waste to update the program every time a download happens.

### Other changes
* Some variables were renamed to better represent what they are
* `applicationType` was removed entirely because there is now a global scope enum class that handles the current running mode of the application (CLI vs GUI).
* Created a 3D look to the program icon.
* In the folder `art` is the Photoshop file of the icon I re created. I re-created it for the new `.icns` file in the `assets/mac` folder which now includes all of the Apple recommended resolutions. I have found that using the higer res set provides a better experience in the Mac environment. I also created a new Windows .ico file which has different resolutions in it as well. The folder also includes the 2D and 3D versions of the icon at different resolutions.
* When the GUI is executed, the running application will have an appropriate icon set in the task bar of the OS, which also displays when `ALT+TAB`ing through running applications.
* Added enums `Type` and `Category` specifically for the `MessageBroker` class so that actual Strings are no longer required for those properties of a message. This eliminates any possibility of sending the wrong `messageType` or `messageCategory` in a message, simplifying the code and hardening the code overall.
	* For example `default` in a `switch` block such as: `default -> logger.log(DriftyConstants.LOGGER_ERROR, "Invalid message messageType provided to message broker!");` is no longer necessary because now it is now impossible to send an invalid messageType.
* In the <b>run</b> method of the <b>FileDownloader</b> class, I changed the default download folder to `System.getProperty("user.home")` instead of `"user.dir"` because every OS will have a user home folder and that makes more sense than putting the download into the same folder that the Drifty executable resides in. 
* The running Mode of the application (being either CLI or GUI) is now established in the enum `Mode`when the program is initially started. This eliminates the need to pass the running mode of the program around in the MessageBroker class.
* All references to the programs running mode have been removed from all methods in the `MessageBroker` class where it now relies on the enum `Mode` to determine what mode the program is running in instead of relying on the value passed in as argument in its methods.
* Can launch the GUI from the command line with either `-g` or `-gui` (not tested)
* Created a Utility class `TheseStrings` to improve code readability where the `Object.equals` method is used. This changes code that reads like this:
	`if (Object.equals(args[i], GUI_FLAG) || (Object.equals(args[i], GUI_FLAG_SHORT)))`
	
	to this:
	`if (TheseStrings.areEqual(args[i], GUI_FLAG) || (TheseStrings.areEqual(args[i], GUI_FLAG_SHORT)))`
	
* Did the same thing for `Objects.requireNonNullElse` changing:
	`Objects.requireNonNullElse(fileName, "%(title)s.%(ext)s");`	
	
	To:
	
	`StringIsNull.replace(fileName, DEFAULT_FILENAME);` 
	
	This is purely for asthetics and can easily be changed back if desired.
