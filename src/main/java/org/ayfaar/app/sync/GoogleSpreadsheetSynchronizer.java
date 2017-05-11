package org.ayfaar.app.sync;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.ayfaar.app.services.GoogleSpreadsheetService;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public class GoogleSpreadsheetSynchronizer<T extends SyncItem> {
    private final HashMap<Integer, BiConsumer<String, String>> columnUpdaters;
    private GoogleSpreadsheetService service;
    private String spreadsheetId;
    private Supplier<Collection<T>> localDataLoader;
    private Function<T, String> keyGetter;
    private boolean skipFirstRow;

    private GoogleSpreadsheetSynchronizer(GoogleSpreadsheetService service, String spreadsheetId) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
        columnUpdaters = new HashMap<>();
    }

    public static <T extends SyncItem> Builder<T> build(GoogleSpreadsheetService service, String spreadsheetId) {
        return new Builder<>(service, spreadsheetId);
    }

    public void sync() throws IOException {
        final Collection<T> localData = localDataLoader.get();
        final List<List<Object>> remoteData = service.read(spreadsheetId, "A:Z");
        if (!remoteData.isEmpty() && skipFirstRow) remoteData.remove(0);

        remoteToLocal(localData, remoteData);
        localToRemote(localData, remoteData);
    }

    private void localToRemote(Collection<T> localData, List<List<Object>> remoteData) throws IOException {
        final int[] index = {remoteData.size() + 1 + (skipFirstRow ? 1 : 0)};

        final ArrayList<T> localToRemoteData = new ArrayList<>();
        localData.forEach(localItem -> {
            final boolean presentOnRemoteSide = remoteData.stream().anyMatch(remoteItem -> keyGetter.apply(localItem).equals(remoteItem.get(0)));
            if (!presentOnRemoteSide) localToRemoteData.add(localItem);
        });
        localToRemoteData.forEach(item -> log.debug("item to remote side: " + item));
        final Map<Integer, List<Object>> batchData = StreamEx.of(localToRemoteData).toMap(item -> index[0]++, SyncItem::toRaw);
        service.write(spreadsheetId, batchData);
    }

    private void remoteToLocal(Collection<T> localData, List<List<Object>> remoteData) {
          remoteData.forEach(remoteItem -> localData.stream()
                  .filter(localItem -> keyGetter.apply(localItem).equals(remoteItem.get(0)))
                  .forEach(localItem -> {
                      final List<Object> localRawItem = localItem.toRaw();
                      for (int i = 1; i < localRawItem.size(); i++) {
                          if (remoteItem.size() <= i) continue;
                          String remoteValue = (String) remoteItem.get(i);
                          String localValue = (String) localRawItem.get(i);
                          if (!Objects.equals(localValue, remoteValue) && !isEmpty(localValue) && !isEmpty(remoteValue) && columnUpdaters.containsKey(i + 1)) {
                              String key = keyGetter.apply(localItem);
                              log.debug("Update from remote. key: {}, column: {}, local value: {}, remote value: {}",
                                      key, i + 1, localValue, remoteValue);
                              columnUpdaters.get(i + 1).accept(key, remoteValue);
                          }
                      }
                  }));
    }

    public void send(String key, int column, String value) throws IOException {
        final List<List<Object>> remoteData = service.read(spreadsheetId, "A:Z");
        if (!remoteData.isEmpty() && skipFirstRow) remoteData.remove(0);

        for (int i = 0; i < remoteData.size(); i++) {
            final List<Object> item = remoteData.get(i);
            if (key.equals(item.get(0))) {
                item.set(column - 1, value);
                service.write(spreadsheetId, i + 1 + (skipFirstRow ? 1 : 0), item);
            }
        }
    }


    public static class Builder<T extends SyncItem> {
        private final GoogleSpreadsheetSynchronizer<T> synchronizer;

        private Builder(GoogleSpreadsheetService service, String spreadsheetId) {
            synchronizer = new GoogleSpreadsheetSynchronizer<>(service, spreadsheetId);
        }

        public Builder<T> keyGetter(Function<T, String> keyGetter) {
            synchronizer.keyGetter = keyGetter;
            return this;
        }

        public Builder<T> columnUpdater(int column, BiConsumer<String, String> updater) {
            synchronizer.columnUpdaters.put(column, updater);
            return this;
        }

        public GoogleSpreadsheetSynchronizer<T> build() {
            return synchronizer;
        }

        public Builder<T> localDataLoader(Supplier<Collection<T>> localDataLoader) {
            synchronizer.localDataLoader = localDataLoader;
            return this;
        }

        public Builder<T> skipFirstRow() {
            synchronizer.skipFirstRow = true;
            return this;
        }

        public Builder<T> direction(SyncDirection direction) {
            // todo
            return this;
        }
    }

    public enum SyncDirection {
        REMOTE_TO_LOCAL, LOCAL_TO_REMOTE, TWO_WAY
    }

}
