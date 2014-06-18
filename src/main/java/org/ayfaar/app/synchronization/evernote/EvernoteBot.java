package org.ayfaar.app.synchronization.evernote;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;
import lombok.Data;
import org.apache.commons.lang3.StringEscapeUtils;
import org.ayfaar.app.model.Item;
import org.htmlcleaner.HtmlCleaner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Lazy
public class EvernoteBot {
//    private static final String SANDBOX_AUTH_TOKEN = "S=s1:U=8ec6c:E=14df1be8cf4:C=1469a0d60f7:P=1cd:A=en-devtoken:V=2:H=95063e0650870375ceab5794def758ce";
    private static final String AUTH_TOKEN = "S=s2:U=23b8d:E=14e070668bd:C=146af553cc3:P=1cd:A=en-devtoken:V=2:H=45afea3eecc7db1ed47916383b5a6827";
    private static final String NOTEBOOK_NAME = "Интерактивная ИИ (автоматический импорт)";
    private static final String QUOTE_TAG_NAME = "цитата";
    private static final String LINK_TAG_NAME = "связь";
    public static final String LINK_EXIST_TAG_NAME = "ссылка существует";
    public static final String TERM_NOT_EXIST_TAG_NAME = "термин несуществует";
    private static final String ALLOWED_TAG_NAME = "проверенно";
    public static final String ITEM_NOT_EXIST_TAG_NAME = "пункт не найден";
    public static final String QUOTE_ALTERED_TAG_NAME = "цитата изменена";
//    public static final String UNDEFINED_TAG_TAG_NAME = "метка не определена";
    public static final String LACK_OF_TERMS_TAG_NAME = "недостаток терминов";
    public static final String NO_TERMS_TAG_NAME = "нет терминов";

    private static final List<String> ERROR_TAGS = Arrays.asList(
            LINK_EXIST_TAG_NAME, TERM_NOT_EXIST_TAG_NAME, ITEM_NOT_EXIST_TAG_NAME, QUOTE_ALTERED_TAG_NAME,
            LACK_OF_TERMS_TAG_NAME, NO_TERMS_TAG_NAME);
    private static final List<String> SERVICE_TAGS = Arrays.asList(
            QUOTE_TAG_NAME, LINK_TAG_NAME, ALLOWED_TAG_NAME);


    private UserStoreClient userStore;
    private NoteStoreClient noteStore;

    public void init() throws Exception {
        if (noteStore != null) return;

        // Set up the UserStore client and check that we can speak to the server
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.PRODUCTION, AUTH_TOKEN);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        userStore = factory.createUserStoreClient();

        boolean versionOk = userStore.checkVersion("II App",
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        if (!versionOk) {
            System.err.println("Incompatible Evernote client protocol version");
            System.exit(1);
        }

        // Set up the NoteStore client
        noteStore = factory.createNoteStoreClient();
    }

    public List<ExportNote> getExportNotes() throws Exception {
        List<ExportNote> exportNotes = new ArrayList<ExportNote>();

        Map<String, String> tagsMap = new HashMap<String, String>();

        List<Notebook> notebooks = noteStore.listNotebooks();
        for (Notebook notebook : notebooks) {
            if (!notebook.getName().equals(NOTEBOOK_NAME)) continue;

            NoteFilter filter = new NoteFilter();
            filter.setNotebookGuid(notebook.getGuid());
            filter.setOrder(NoteSortOrder.CREATED.getValue());
            filter.setAscending(true);

            NoteList noteList = noteStore.findNotes(filter, 0, 1000);
            List<Note> notes = noteList.getNotes();

            for (Note note : notes) {
                if (note.getTagGuids() == null) continue;

                List<String> tags = new ArrayList<String>();
                for (String tagGuid : note.getTagGuids()) {
                    String tagName = tagsMap.get(tagGuid);
                    if (tagName == null) {
                        Tag tag = noteStore.getTag(tagGuid);
                        tagName = tag.getName();
                        tagsMap.put(tagGuid, tagName);
                    }
                    tags.add(tagName);
                }
                if (tags.contains(QUOTE_TAG_NAME)) {
                    QuoteLink link = new QuoteLink();
                    link.setAllowed(tags.contains(ALLOWED_TAG_NAME));
                    boolean conflictedLink = false;
                    for (String tag : tags) {
                        if (ERROR_TAGS.contains(tag) && !link.getAllowed()) {
                            conflictedLink = true;
                            break;
                        } else if (SERVICE_TAGS.contains(tag) || ERROR_TAGS.contains(tag)) {
                            // skip
                        } else if (Item.isItemNumber(tag)) {
                            link.setItem(tag);
                        } else {
                            link.getTerms().add(tag);
                        }
                    }
                    if (conflictedLink) continue; // skip link on conflicted
                    if (link.getTerms().size() == 0) {
                        setTag(note.getGuid(), NO_TERMS_TAG_NAME);
                        continue;
                    }

                    String text = noteStore.getNoteContent(note.getGuid());
                    if (text != null && !text.isEmpty()) {
                        text = new HtmlCleaner().clean(text).getText().toString();
                        text = StringEscapeUtils.unescapeHtml4(text);
                        link.setQuote(text);
                    }
                    link.setGuid(note.getGuid());
                    exportNotes.add(link);
                } else if (tags.contains(LINK_TAG_NAME)) {
                    RelatedTerms related = new RelatedTerms();
                    related.setAllowed(tags.contains(ALLOWED_TAG_NAME));
                    related.setType(null);
                    related.setGuid(note.getGuid());

                    boolean conflictedLink = false;
                    for (String tag : tags) {
                        if (ERROR_TAGS.contains(tag) && !related.getAllowed()) {
                            conflictedLink = true;
                            break;
                        } else if (!SERVICE_TAGS.contains(tag) && !ERROR_TAGS.contains(tag)) {
                            related.getTerms().add(tag);
                        }
                    }
                    if (conflictedLink) continue; // skip link on conflicted

                    related.getTerms().addAll(Arrays.asList(note.getTitle().split("  ")));
                    if (related.getTerms().size() == 0) {
                        setTag(note.getGuid(), NO_TERMS_TAG_NAME);
                        continue;
                    }
                    exportNotes.add(related);
                }

            }
        }
        return exportNotes;
    }

    public void removeNote(String guid) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        noteStore.deleteNote(guid);
    }

    public void setTag(String guid, String tag) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        Note note = noteStore.getNote(guid, false, false, false, false);
        note.addToTagNames(tag);
        noteStore.updateNote(note);
    }

    @Data
    public class QuoteLink extends ExportNote {
        private String item;
        private Set<String> terms = new HashSet<String>();
        private String quote;
    }

    @Data
    public class RelatedTerms extends ExportNote {
        private Byte type;
        private List<String> terms = new ArrayList<String>();
    }

    @Data
    public class ExportNote {
        private String guid;
        private Boolean allowed;
    }
}
