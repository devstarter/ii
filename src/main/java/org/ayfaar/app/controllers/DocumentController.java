package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Document;
import org.ayfaar.app.utils.GoogleService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.ayfaar.app.utils.UriGenerator.generate;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("api/document")
//todo: rename, update author etc
public class DocumentController {
    @Inject CommonDao commonDao;
    @Inject GoogleService googleService;

    @RequestMapping(method = RequestMethod.POST)
    public Document create(@RequestParam String url,
                           @RequestParam(required = false) Optional<String> name,
                           @RequestParam(required = false) String author,
                           @RequestParam(required = false) String annotation) {
        Assert.hasLength(url);
        final String docId = GoogleService.extractDocIdFromUrl(url);
        return commonDao.getOpt(Document.class, generate(Document.class, docId))
            .orElseGet(() -> {
                final GoogleService.DocInfo docInfo = googleService.getDocInfo(docId);
                final Document document = Document.builder()
                        .id(docId)
                        .name(name.orElse(docInfo.title))
                        .annotation(annotation)
                        .author(author)
                        .thumbnail(docInfo.thumbnailLink)
                        .mimeType(docInfo.mimeType)
                        .icon(docInfo.iconLink)
                        .downloadUrl(docInfo.downloadUrl)
                        .build();
                return commonDao.save(document);
            });

    }

    @RequestMapping("{id}")
    public Document get(@PathVariable String id) {
        return commonDao.get(Document.class, generate(Document.class, id));
    }

    @RequestMapping("last")
    public List<Document> getLast(@PageableDefault(size = 9, sort = "createdAt", direction = DESC) Pageable pageable) {
        return commonDao.getPage(Document.class, pageable);
    }
}
