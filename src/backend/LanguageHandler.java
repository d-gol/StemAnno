package backend;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class LanguageHandler {
	private static Properties prop;
	private static String baseFilename;
	private static String language;
	
	/*public LanguageHandler(String base, String lang) throws Exception {
		baseFilename = base;
		language = lang;
		
		readPropertiesFile();
	}*/
	
	public static void initialize(String base, String lang) throws Exception {
		baseFilename = base;
		language = lang;
		
		readPropertiesFile();
	}

	private static void readPropertiesFile() throws Exception {
		InputStream input = null;
		input = LanguageHandler.class.getResourceAsStream(baseFilename + "_" + language + ".properties");
		prop = new Properties();
		prop.load(new InputStreamReader(input, Charset.forName("UTF-8")));
	}
	
	public static void setLanguage(String lang) throws Exception {
		if (!language.equals(lang)) {
			language = lang;
			readPropertiesFile();
		}
	}
	
	public static String getLanguage() {
		return language;
	}
	
	public static String getMessage(String key) {
		return prop.getProperty(key);
	}
}
