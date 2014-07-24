package org.ayfaar.ii.dao;

import org.ayfaar.ii.model.Song;

public interface SongDao extends BasicCrudDao<Song> {
    Song getByName(String name);
    Song getById(Integer songId);
    Song getSongHtml(Integer songId);
}