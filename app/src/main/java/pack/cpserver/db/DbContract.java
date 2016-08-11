package pack.cpserver.db;

import android.provider.BaseColumns;

import pack.cpserver.cp.ContentContract;

public interface DbContract {
    String DB_NAME = "artists.db";

    String ARTISTS = ContentContract.ARTISTS;
    String GENRES = ContentContract.Artists.GENRES;
    String ARTISTS_GENRES = "ARTISTS_GENRES";
    String GENRES_JOIN_DELIMITER = ContentContract.GENRES_JOIN_DELIMITER;
    String ARTISTS_WITH_GENRES = "ARTISTS_WITH_GENRES";

    interface Artists extends BaseColumns {
        String
                NAME = ContentContract.Artists.NAME,
                TRACKS = ContentContract.Artists.TRACKS,
                ALBUMS = ContentContract.Artists.ALBUMS,
                LINK = ContentContract.Artists.LINK,
                DESCRIPTION = ContentContract.Artists.DESCRIPTION,
                SMALL_COVER = ContentContract.Artists.SMALL_COVER,
                BIG_COVER = ContentContract.Artists.BIG_COVER;
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
