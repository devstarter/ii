package org.ayfaar.app.dao;

import org.ayfaar.app.model.Song;

public interface SongDao extends BasicCrudDao<Song> {
    Song getByName(String name);
    Song getById(Integer songId);
    Song getSongHtml(Integer songId);
}