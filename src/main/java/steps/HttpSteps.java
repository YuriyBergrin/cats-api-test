package steps;

import configs.ApiConfig;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class HttpSteps extends ApiConfig {
	protected HashMap<String, String> report = new HashMap<>();
	Response response;

	@Step("Выполняем  GET запрос к {endpoint} с параметром {paramName} равным {paramValue}")
	protected void getRequest(String endpoint, String paramName, String paramValue) {
		response = given()
				.spec(catsApiRequestSpecification)
				.log()
				.uri()
				.param(paramName, paramValue)
				.when()
				.get(endpoint);
		returnString(response.asString());
	}

	@Step("Выполняем  GET запрос к {endpoint}")
	protected void getRequest(String endpoint) {
		response = given()
				.spec(catsApiRequestSpecification)
				.log()
				.uri()
				.when()
				.get(endpoint);
		returnString(response.asString());
	}

	@Step("Выполняем POST запрос к {endpoint} c body равным: {requestBody}")
	protected void postRequest(String endpoint, String requestBody) {
		response = given()
				.spec(catsApiRequestSpecification)
				.body(requestBody)
				.log()
				.uri()
				.when()
				.post(endpoint);
		returnString(response.asString());
	}

	@Step("Выполняем DELETE запрос к {endpoint}")
	protected void deleteRequest(String endpoint) {
		response = given()
				.spec(catsApiRequestSpecification)
				.log()
				.uri()
				.when()
				.delete(endpoint);
		returnString(response.asString());
	}

	@Step("Получаем значение по json path равному {jsonpath}")
	protected String getValueByPath(String jsonpath) {
		String value = response.path(jsonpath).toString();
		returnString(value);
		return value;
	}

	@Step("Проверяем, что значение в ответе по пути равному {jsonpath} равно значению переменной {value}")
	protected void assertValueByPath(String value, String jsonpath) {
		Assert.assertEquals(value, response.path(jsonpath));
		returnString(response.path(jsonpath));
	}

	@Step("Проверяем, что в ответе отсуствует обект по заданному пути {jsonpath}")
	protected void checkDoesNotExistObject(String jsonpath) {
		String val = response.path(jsonpath);
		Assert.assertNull(val);
	}

	@Step("Проверяем, что в ответе существует обект по заданному пути {jsonpath} со значением {value}")
	protected void checkExistObject(String value, String jsonpath) {
		Assert.assertTrue(response.path(jsonpath).toString().contains(value));
	}

	@Step("Статус-код ответа должен быть равен {statusCode}")
	public void checkStatusCode(int statusCode) {
		Assert.assertEquals(statusCode, response.getStatusCode());
	}

	@Attachment(value = "result", type = "application/json")
	public String returnString(String string) {
		return string;
	}

	@After
	public void makeReportFile() throws IOException {
		try (PrintWriter writer = new PrintWriter(new File("report.txt"))) {
			for (Map.Entry<String, String> entry : report.entrySet()) {
				writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getReportFile();
	}

	@Attachment(value = "Отчет", type = "application/json", fileExtension = ".txt")
	public static byte[] getReportFile() throws IOException {
		return Files.readAllBytes(Paths.get("report.txt"));
	}

	@Step("Сравним ответ сервиса с json-файлом")
	public void assertResponseByFile(String filePath) {
		try {
			JSONAssert.assertEquals(new String(Files.readAllBytes(Paths.get(filePath))), response.asString(), JSONCompareMode.LENIENT);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

	}
}
