package org.ayfaar.ii.importing;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SongsImporter {

        private void parseSongsContentList() {
            // todo: extract song page numbers and song categories
        }

        // if the formatting of the song file is bad - here can crash
        private String parseSongsFile(String fileName) throws IOException {
            String sqlQuery = "DROP TABLE IF EXISTS `song`;		\n"+
                    "CREATE TABLE IF NOT EXISTS `song` (	\n"+
                    "  `id` int(5) NOT NULL AUTO_INCREMENT,	\n"+
                    "  `name` varchar(255) DEFAULT NULL,	\n"+
                    "  `info` varchar(255) DEFAULT NULL,	\n"+
                    "  `content` text,						\n"+
                    "  PRIMARY KEY (`id`)					\n"+
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;	\n"+
                    "INSERT INTO `song` (`name`, `info`, `content`) VALUES\n";

            String name="", info="", content="";
            for(String line: FileUtils.readLines(new File(fileName))) {
                if(line.isEmpty()){
                    // delete last newline
                    name = name.substring(0, name.length()-1);
                    info = info.substring(0, info.length()-1);
                    content = content.substring(0, content.length()-1);
                    sqlQuery += "('" + name + "', '" + info + "', '" + content + "'),\n";
                    name = info = content = "";
                    continue;
                }
                // song name block
                // for first line in file the ENCODING must be UTF8 WITHOUT BOM!!!
                if(line.matches("(?u)^[«»А-ЯЁ -]+$")){
                    name += line + "\n";
                    continue;
                }
                // start info block
                if( line.startsWith("(") || line.endsWith(")") ){
                    info += line + "\n";
                    continue;
                }
                // content
                content += line + "\n";
            }
            // add last song, because it ends without "line.isEmpty"
            name = name.substring(0, name.length()-1);
            info = info.substring(0, info.length()-1);
            content = content.substring(0, content.length()-1);
            sqlQuery += "('" + name + "', '" + info + "', '" + content + "');\n";
            return sqlQuery;
        }

        public static void main(String[] args) throws IOException {
            FileOutputStream fileStream = new FileOutputStream(new File("C:\\tmp\\songs.sql"));
            OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
            SongsImporter songs = new SongsImporter();
            String sqlSongs  = songs.parseSongsFile("C:\\ii_project\\ii\\src\\main\\text\\MZ2.txt");
            writer.write(sqlSongs);
            writer.close();
        }

}
