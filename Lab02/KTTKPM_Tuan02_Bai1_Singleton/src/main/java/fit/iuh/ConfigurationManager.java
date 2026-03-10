package fit.iuh;

public class ConfigurationManager {
    private static volatile ConfigurationManager instance;
    private String configValue = "Default Config";

    private ConfigurationManager() {}

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public void setConfig(String value) { this.configValue = value; }
    public String getConfig() { return configValue; }
}
