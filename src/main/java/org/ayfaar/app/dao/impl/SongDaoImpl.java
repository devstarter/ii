package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.SongDao;
import org.ayfaar.app.model.Song;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ilike;

@Repository
public class SongDaoImpl extends AbstractHibernateDAO<Song> implements SongDao {
    public SongDaoImpl() {
        super(Song.class);
    }

    @Override
    public Song getByName(String name) {
        return (Song) criteria()
                .add(ilike("name", name))
                .uniqueResult();
    }

    @Override
    public Song getById(Integer songId) {
        return (Song) criteria()
                .add(eq("id", songId))
                .uniqueResult();
    }

    @Override
    @Deprecated
    public Song getSongHtml(Integer songId){
        String songHtml="";
        Song song = getById(songId);
        String lines[] = song.getContent().split("\\n");

        int tabs, spaces;
        boolean inRepeatBlock_new = false, inRepeatBlock_old = false;
        boolean inRefrain_new = false, inRefrain_old = false;
        for(String line: lines) {
            tabs = spaces = 0;
            inRepeatBlock_old = inRepeatBlock_new;
            inRefrain_old = inRefrain_new;
            // first 7 symbols set the format of a html line: 2 tabs + 5 spaces
            for (int i = 0; i < 7 && i < line.length(); i++) {
                char c = line.charAt(i);
                if(c == ' '){
                    ++spaces;
                }
                else{
                    if(c == '\t'){
                        ++tabs;
                    }
                    else{
                        break;
                    }
                }
            }
            // refrain block
            if(tabs == 1){
                inRefrain_new = true;
            }
            else{
                inRefrain_new = false;
            }
            // repeat block
            if(tabs == 2){
                inRepeatBlock_new = true;
            }
            else{
                inRepeatBlock_new = false;
            }
            // handle refrain html-tags
            if(inRefrain_new == true && inRefrain_old == false){
                songHtml += "<div class=\"refrainBlock\">";
            }
            if(inRefrain_new == false && inRefrain_old == true){
                songHtml += "</div>";
            }
            // handle repeat html-tags
            if(inRepeatBlock_new == true && inRepeatBlock_old == false){
                songHtml += "<div class=\"repeatBlock\">";
            }
            if(inRepeatBlock_new == false && inRepeatBlock_old == true){
                songHtml += "</div>";
            }
            songHtml += "<div class=\"tabs"+ tabs +" spaces"+ spaces +"\"><span>"+ line.trim() + "</span></div>";
        }
        // last line is in refrain block as well
        if(inRefrain_new == true && inRefrain_old == true){
            songHtml += "</div>";
        }
        // last line is in repeat block as well
        if(inRepeatBlock_new == true && inRepeatBlock_old == true){
            songHtml += "</div>";
        }
        song.setName(song.getName().replaceAll("\\n", "</br>"));
        song.setInfo(song.getInfo().replaceAll("\\n", "</br>"));
        song.setContent(songHtml);
        return song;
    }
}