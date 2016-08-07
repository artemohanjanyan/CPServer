package pack.cpserver.db;

interface DbContract {
    String DB_NAME = "artists.db";

    String ARTISTS = "artists";
    interface Artists {
        String
                ID = "_ID",
                NAME = "NAME",
                TRACKS = "TRACKS",
                ALBUMS = "ALBUMS",
                LINK = "LINK",
                DESCRIPTION = "DESCRIPTION",
                SMALL_COVER = "SMALL_COVER",
                BIG_COVER = "BIG_COVER";
    }

    String GENRES = "genres";
    interface Genres {
        String
                ID = "_ID",
                NAME = "NAME";
    }

    String ARTISTS_GENRES = "artists_genres";
    interface ArtistsGenres {
        String
                ARTISTS_ID = "ARTISTS_ID",
                GENRES_ID = "GENRES_ID";
    }

    String GENRES_JOIN_DELIMITER = "$";
}
