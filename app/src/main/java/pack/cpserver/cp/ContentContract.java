package pack.cpserver.cp;

import android.provider.BaseColumns;

import pack.cpserver.db.DbContract;

@SuppressWarnings("WeakerAccess")
public interface ContentContract {
    @SuppressWarnings("SpellCheckingInspection")
    String AUTHORITY = "pack.cpserver.cp.ContentProvider";

    String ARTISTS = DbContract.ARTISTS;

    interface Artists extends BaseColumns {
        @SuppressWarnings("unused")
        String
                NAME = DbContract.Artists.NAME,
                TRACKS = DbContract.Artists.TRACKS,
                GENRES = DbContract.GENRES,
                ALBUMS = DbContract.Artists.ALBUMS,
                LINK = DbContract.Artists.LINK,
                DESCRIPTION = DbContract.Artists.DESCRIPTION,
                SMALL_COVER = DbContract.Artists.SMALL_COVER,
                BIG_COVER = DbContract.Artists.BIG_COVER;
    }
}
