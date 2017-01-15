package org.ayfaar.app.services.translations;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Translation;
import org.ayfaar.app.translation.TranslationItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class TranslationService {
	private CommonDao commonDao;
	private List<Translation> translations;

	@Autowired
	public TranslationService(CommonDao commonDao) {
		this.commonDao = commonDao;
	}

	@PostConstruct
	public void init() {
		log.info("Translations loading...");
		translations = commonDao.getAll(Translation.class);
		log.info("Translations loaded");
	}

	public List<Translation> getAll() {
		return translations;
	}

	public Stream<TranslationItem> getAllAsTranslationItem() {
		return getAll().stream().map(t -> new TranslationItem(t.getOrigin(), t.getTranslated()));
	}

	public Translation save(Translation translation) {
		return commonDao.save(translation);
	}
}
