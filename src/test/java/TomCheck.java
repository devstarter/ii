import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.utils.TermService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class TomCheck extends IntegrationTest {

    @Autowired
    CommonDao commonDao;
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    TermService termService;

    Set<TermService.TermProvider> contains = new HashSet<>();
    AtomicReference<Integer> lineCounter = new AtomicReference<>();

    @Test
    public void parse() throws Exception {
        File textFile = resourceLoader.getResource("classpath:6 tom.txt").getFile();
        File termsFile = resourceLoader.getResource("file:c:\\projects\\ayfaar\\ii-app\\src\\test\\resources\\terms.txt").getFile();

        try (Stream<String> stream = Files.lines(textFile.toPath())) {

            stream.parallel().forEach(this::findTerms);

        } catch (IOException e) {
            e.printStackTrace();
        }
        List<TermService.TermProvider> sorted = new ArrayList<>(contains);
        sorted.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        if (!termsFile.exists()) termsFile.createNewFile();

        Files.write(termsFile.toPath(), sorted.stream()
                .map(TermService.TermProvider::getName)
                .collect(Collectors.toList()), StandardOpenOption.WRITE);
    }

    private void findTerms(String line) {
        contains.addAll(termService.findTerms(line).values());
    }

}
