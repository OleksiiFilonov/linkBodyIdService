package oleksii.filonov;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestConstants {
	private static final Path TARGET_RESOURCE = Paths.get("", "target");
	private static final Path RESOURCE_PATH = Paths.get("", "src", "test", "resources");
	public static final Path LINKED_RESULT_PATH = TARGET_RESOURCE.resolve("resultLinks.xls");
	public static final File CAMPAIGN_FILE = RESOURCE_PATH.resolve("Campaign.xlsx").toFile();
	public static final File CLIENT_FILE = RESOURCE_PATH.resolve("Clients.xls").toFile();
	public static final File CAMPAIGN_FILE2 = RESOURCE_PATH.resolve("Campaign2.xlsx").toFile();
	public static final File CLIENT_FILE2 = RESOURCE_PATH.resolve("Clients2.xlsx").toFile();
}
