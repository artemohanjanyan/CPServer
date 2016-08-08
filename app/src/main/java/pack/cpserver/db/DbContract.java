package pack.cpserver.db;

import android.provider.BaseColumns;

public interface DbContract {
    String DB_NAME = "artists.db";

    String ARTISTS = "ARTISTS";
    String GENRES = "GENRES";
    String ARTISTS_GENRES = "ARTISTS_GENRES";
    String GENRES_JOIN_DELIMITER = "$";

    interface Artists extends BaseColumns {
        String
                NAME = "NAME",
                TRACKS = "TRACKS",
                ALBUMS = "ALBUMS",
                LINK = "LINK",
                DESCRIPTION = "DESCRIPTION",
                SMALL_COVER = "SMALL_COVER",
                BIG_COVER = "BIG_COVER";
    }

    interface Genres extends BaseColumns {
        String
                NAME = "NAME";
    }

    interface ArtistsGenres {
        String
                ARTISTS_ID = "ARTISTS_ID",
                GENRES_ID = "GENRES_ID";
    }
}
