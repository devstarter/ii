package org.ayfaar.app;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.services.videoResource.VideoResourceService;
import org.ayfaar.app.utils.GoogleService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import javax.inject.Inject;

@Slf4j
@ActiveProfiles("remote")
@Ignore
public class VideoSetCodesAction extends IntegrationTest {
    @Inject VideoResourceService videoService;
    @Inject GoogleService googleService;
    @Inject CommonDao commonDao;

    @Test
    public void setCodes() {
        videoService.getAll()
                .filter(v -> StringUtils.isEmpty(v.getCode()))
                .forEach(v -> {
                    try {
                        googleService.getCodeVideoFromYoutube(v.getId())
                                .ifPresent(code -> {
                                    v.setCode(code);
                                    commonDao.save(v);
                                    log.info("{}: {}", code, v.getTitle());
                                });
                    } catch (Exception ignore) {
                        log.warn("", ignore);
                    }
                });
    }
}
