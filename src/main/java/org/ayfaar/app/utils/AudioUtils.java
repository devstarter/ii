package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class AudioUtils {
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	public static Metadata getMp3Metadata(File file) {
		ContentHandler handler = new DefaultHandler();
		Metadata metadata = new Metadata();
		Parser parser = new Mp3Parser();
		ParseContext parseCtx = new ParseContext();
		try {
			// parse metadata
			InputStream input = new FileInputStream(file);
			parser.parse(input, handler, metadata, parseCtx);
			input.close();
		} catch (IOException | SAXException | TikaException e) {
			log.error("Can't get file {} metadata", file.getAbsolutePath(), e);
		}

		return metadata;
	}

	public static Metadata getMp3MetadataFromUrl(String url) {
		String[] urlPart = url.split("/");
		String fileName = java.util.UUID.randomUUID().toString() + ".mp3";
		String filePath = TEMP_DIR + "/" + fileName;
		Metadata metadata = new Metadata();
		try {
			// copy URL to file
			log.info("Copy from {} to {}", url, filePath);
			URL link = new URL(url);
			File file = new File(filePath);
			FileUtils.copyURLToFile(link, file);
			// get metadata
			metadata = getMp3Metadata(file);
			if (file.delete()) {
				log.info("Deleted file: {}", filePath);
			}
		} catch (IOException e) {
			log.error("Can't copy from {} to {}", url, filePath, e);
		}

		return metadata;
	}

	public static Integer getMp3Duration(File file) {
		Metadata metadata = getMp3Metadata(file);
		if (metadata != null) {
			return (int) Double.parseDouble(metadata.get("xmpDM:duration")) / 1000;
		}
		return -1;
	}

	public static int getMp3DurationFromUrl(String url) {
		Metadata metadata = getMp3MetadataFromUrl(url);
		if (metadata != null) {
			return (int) Double.parseDouble(metadata.get("xmpDM:duration")) / 1000;
		}
		return -1;
	}
}
