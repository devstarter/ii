package org.ayfaar.app.utils;

import lombok.Data;
import lombok.Getter;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.Collections.sort;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;


@Component
public class TopicServiceImpl implements TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private CommonDao commonDao;
    @Autowired
    private LinkDao linkDao;

    private Map<String, LinkInfo> links;
    private Map<String, TopicProvider> aliasesMap;
    private ArrayList<Map.Entry<String, TopicProvider>> sortedList;

    @Data
    private class LinkInfo {
        private byte type;
        private Topic mainTopic;

        private LinkInfo(byte type, Topic topic) {
            this.type = type;
            this.mainTopic = topic;
        }
    }

    public class TopicProviderImpl implements TopicProvider{

        @Getter
        private String uri;
        private String mainTopicUri;

        public TopicProviderImpl(String uri, String mainTopicUri) {
            this.uri = uri;
            this.mainTopicUri = mainTopicUri;
        }

        @Override
        public String getName() {
            return getValueFromUri(Topic.class, uri);
        }

//        @Override
//        public String getUri() {
//            return null;
//        }

        @Override
        public TopicProvider getMainTopicProvider() {
            return hasMainTopic() ? aliasesMap.get(getValueFromUri(Topic.class, mainTopicUri).toLowerCase()) : null;
        }

        @Override
        public Topic getTopic() {
            return commonDao.get(Topic.class, uri);
        }

        private List<TopicProvider> getListProviders(byte type, String name) {
            List<TopicProvider> providers = new ArrayList<TopicProvider>();

            for(Map.Entry<String, LinkInfo> link : links.entrySet()) {
                if(link.getValue().getType() == type && link.getValue().getMainTopic().getName().equals(name)) {
                    providers.add(aliasesMap.get(getValueFromUri(Topic.class, link.getKey().toLowerCase())));
                }
            }
            return providers;
        }


        @Override
        public Byte getType() {
            return links.get(uri) != null ? links.get(uri).getType() : null;
        }

        @Override
        public boolean hasMainTopic() {
            return mainTopicUri != null;
        }

//
    }

    @Override
    public List<Map.Entry<String, TopicProvider>> getAll() {
        return sortedList;
    }

    @Override
    public TopicProvider getTopicProvider(String name) {
        return aliasesMap.get(name.toLowerCase());
    }

    @Override
    public Topic getTopic(String name) {
        TopicProvider topicProvider = aliasesMap.get(name.toLowerCase());
        return topicProvider != null ? topicProvider.getTopic() : null;
    }

//    @Override
//    public void reload() {
//        load();
//    }

//    @PostConstruct
//    private void load() {
//        logger.info("Topic map loading...");
//        aliasesMap = new HashMap<String, TopicProvider>();
//
//
//        //List<commonDao.TopicInfo> topicInfo = commonDao.getAllTopicInfo();
//        List<Link> allSynonyms = linkDao.getAllSynonyms();
//
//        links = new HashMap<String, LinkInfo>();
//        for(Link link : allSynonyms) {
//            links.put(link.getUid2().getUri(), new LinkInfo(link.getType(), (Topic)link.getUid1()));
//        }

//        for(commonDao.TopicInfo info : topicInfo) {
//            String uri = UriGenerator.generate(Topic.class, info.getName());
//            String mainTopicUri = null;
//
//            if(links.containsKey(uri)) {
//                mainTopicUri = links.get(uri).getMainTopic().getUri();
//            }
//            aliasesMap.put(info.getName().toLowerCase(), new TopicProviderImpl(uri, mainTopicUri));
//        }



        // prepare sorted List by term name length, longest terms first
//        sortedList = new ArrayList<Map.Entry<String, TopicService.TopicProvider>>(aliasesMap.entrySet());
//        sort(sortedList, new Comparator<Map.Entry<String, TopicService.TopicProvider>>() {
//            @Override
//            public int compare(Map.Entry<String, TopicService.TopicProvider> o1, Map.Entry<String, TopicService.TopicProvider> o2) {
//                return Integer.compare(o2.getKey().length(), o1.getKey().length());
//            }
//        });
//        logger.info("Topic map loading finish");
//    }
}
