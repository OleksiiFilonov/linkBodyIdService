package oleksii.filonov;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestConstants {
	public static final Path TARGET_RESOURCE = Paths.get("", "target");
	public static final Path LINKED_RESULT_PATH = TARGET_RESOURCE.resolve("resultLinks.xls");
	public static final File CAMPAIGN_FILE = Paths.get("", "src", "test", "resources", "Campaign.xlsx").toFile();
	public static final File CLIENT_FILE = Paths.get("", "src", "test", "resources", "Clients.xls").toFile();
}
