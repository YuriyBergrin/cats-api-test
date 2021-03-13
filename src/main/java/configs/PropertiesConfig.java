package configs;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("classpath:config.properties")
public interface PropertiesConfig extends Config {
	@Key("baseurl")
	String baseurl();
	@Key("basepath")
	String basepath();
}
