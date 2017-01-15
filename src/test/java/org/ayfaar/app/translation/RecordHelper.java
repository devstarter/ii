package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.services.record.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RecordHelper {
	RecordService recordService;

	@Autowired
	public RecordHelper(RecordService recordService) {
		this.recordService = recordService;
	}

	public void reloadRecordsDuration() {
		log.info("Reloading record duration start");

		List<Record> records = recordService.getAll();
		records.parallelStream()
				.filter(r -> StringUtils.isNotEmpty(r.getAudioUrl()))
				.peek(r -> r.setDuration(AudioUtils.getMp3DurationFromUrl(r.getAudioUrl())))
				.forEach(recordService::save);

		log.info("Reloading record duration end");
	}

	public void setRecordsDuration() {
		log.info("Setting record duration start");

		List<Record> records = recordService.getAll();
		records.parallelStream()
				.filter(r -> StringUtils.isNotEmpty(r.getAudioUrl()) && (r.getDuration() == null || r.getDuration() <= 0))
				.peek(r -> r.setDuration(AudioUtils.getMp3DurationFromUrl(r.getAudioUrl())))
				.forEach(recordService::save);

		log.info("Setting record duration end");
	}
}
