package org.ayfaar.app.translation;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.services.TranslationService;
import org.ayfaar.app.utils.Language;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;

public class TranslationServiceTest extends IntegrationTest {
    @Inject TranslationService service;

    @Test
    public void test() {
        String origin = "Почему же и каким образом в Фокусной Динамике высвобождается крувурсорртная Информация и каким именно образом выделившийся (высвободившийся) крувурсорртный информационный Потенциал преобразуется в декогерентную Энергию? Если мы рассмотрим два участка фокусной Конфигурации, которые имеют некую изначальную степень имперсептности, то для образования максимально коварллертного состояния между ними Формо-Творцы обоих участков должны будут качественно переструктурировать свойственное им информационное наполнение (СФУУРММ-Формы) путём исключения из них тех инфо-фрагментов, которые вызывают диссонанс в результирующей (объединённой) Конфигурации, а вместо высвободившихся взаимосвязей привлечь в свои ф-Конфигурации некие дополнительные инфо-фрагменты, коварллертные по отношению к каждому из участков, которые бы послужили в качестве «связующих звеньев» (в привычном для нас понимании это означает – найти консенсус между разными, порой даже противоположными, интересами).";
        String translated = service.translate(origin, Language.en);
        Assert.assertNotNull(translated);
    }
}
