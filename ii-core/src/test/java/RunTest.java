import org.junit.Assert;
import org.ayfaar.ii.model.Article;
import org.junit.Test;

/**
 * Created by yurec on 10.07.2014.
 */
public class RunTest {
    @Test
    public void test() {
        Article article = new Article();
        Assert.assertNotNull(article);
    }
}
