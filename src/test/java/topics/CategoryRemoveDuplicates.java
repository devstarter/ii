package topics;


import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@Ignore
public class CategoryRemoveDuplicates extends IntegrationTest {

    @Inject
    CategoryDao categoryDao;
    @Inject
    CommonDao commonDao;

    @Test
    public void getDuplicates(){
        List<Category> all = categoryDao.getAll();
        all.stream().forEach(category -> {
            for (Category s : all){
                if (category.getUri()!=null&&category.getUri().replaceAll(" ","").equals(s.getUri().replaceAll(" ",""))){
                    if(s.getUri().length() < category.getUri().length()){
                        log.info(category.getUri());
                        commonDao.remove(category);
                    }
                }
            }
        });
    }

}
