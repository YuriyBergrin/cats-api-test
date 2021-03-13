package configs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;

public class ApiConfig {
	protected PropertiesConfig cfg = ConfigFactory.create(PropertiesConfig.class);

	protected RequestSpecification catsApiRequestSpecification = new RequestSpecBuilder()
			.setBaseUri(cfg.baseurl())
			.setBasePath(cfg.basepath())
			.addHeader("x-api-key", System.getProperty("apikey"))
			.addHeader("Content-Type", "application/json")
			.build();
}
