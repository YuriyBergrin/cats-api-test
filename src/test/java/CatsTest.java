import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.Assert;
import org.junit.Test;
import steps.HttpSteps;

public class CatsTest extends HttpSteps {
	@Feature(value = "Тесты кошачего api")
	@Description(value = "Функционал - избранное")
	@Test
	public void favoriteTest() {
		getRequest("breeds/search", "q", "Scottish Fold");
		String breedId = getValueByPath("[0].id");
		getRequest("images/search", "breed_id", breedId);
		assertValueByPath(breedId, "[0].breeds[0].id");
		String imageId = getValueByPath("[0].id");
		String imageUrl = getValueByPath("[0].url");
		String requestBody = String.format("{\"image_id\":\"%s\"}", imageId);
		postRequest("favourites", requestBody);
		assertValueByPath("SUCCESS", "message");
		String favourId = getValueByPath("id");
		getRequest("favourites");
		assertValueByPath(imageId, String.format("find{it.id==%s}.image_id", favourId));
		deleteRequest("favourites/" + favourId);
		assertValueByPath("SUCCESS", "message");
		getRequest("favourites");
		checkDoesNotExistObject(String.format("find{it.id==%s}", favourId));
		report.put("Scottish Fold", breedId);
		report.put("Image url", imageUrl);
	}

	@Description(value = "Функционал - категории")
	@Test
	public void categoryTest() {
		getRequest("categories/");
		checkExistObject("boxes", "name");
	}

}
