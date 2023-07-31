package Preferences;

/**
 * This is main class that is used to access any of the Preferences which can store
 * information that remains consistent and intact between program reloads.
 * <p>
 * Here is how you use it:
 * <p>
 * The LABEL class is an enum class. You create the label of the Preference there.
 * <p>
 * The Clear class is used to clear the property and remove it from the Properties
 * database. I have found that anytime you change a Preference, it must first be
 * cleared before the change can "stick". At least that has been my experience with
 * the Preferences class.
 * <p>
 * The Set class sets the preference.
 * <p>
 * The Get class gets the preference.
 * <p>
 * The method names in the clear, set and get classes are all identical so that writing
 * code for preferences is an easier task. All one needs to do is leverage context-sensitive
 * features in the IDE and just type AppSettings dot get or set then dot
 * and the preference methods will pop up. For example, to set the updateTimestamp
 * property, one only needs do this:
 * <p>
 * AppSettings.set.updateTimeStamp(long);
 * <p>
 * Getting that value:
 * <p>
 * AppSettings.get.updateTimeStamp();
 * <p>
 * The LABEL enum has a method to get the name for each enum that is a String which
 * is needed by the Preferences class.
 *
 */

public class AppSettings {

    public static final Get get = Get.INSTANCE;
    public static final Set set = Set.INSTANCE;
    public static final Clear clear = Clear.INSTANCE;

}
