import org.junit.Assert;
import org.ayfaar.app.model.Article;
import org.junit.Test;

public class RunTest {
    @Test
    public void test() {
        Article article = new Article();
        Assert.assertNotNull(article);
    }
}
