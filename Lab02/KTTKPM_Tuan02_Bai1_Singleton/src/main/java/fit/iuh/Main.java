package fit.iuh;

public class Main {
    public static void main(String[] args) {
        ConfigurationManager config1 = ConfigurationManager.getInstance();
        ConfigurationManager config2 = ConfigurationManager.getInstance();

        config1.setConfig("Server: 192.168.1.1");

        System.out.println(config2.getConfig());
        System.out.println(config1 == config2);
    }
}