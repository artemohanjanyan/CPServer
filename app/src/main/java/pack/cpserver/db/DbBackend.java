package pack.cpserver.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import solid.collections.Pair;
import solid.collections.SolidList;
import solid.collections.SolidMap;
import solid.collectors.ToSolidMap;
import solid.collectors.ToSolidSet;
import solid.stream.Stream;

import static pack.cpserver.db.DbContract.ARTISTS;
import static pack.cpserver.db.DbContract.ARTISTS_WITH_GENRES;
import static pack.cpserver.db.DbContract.Artists;
import static pack.cpserver.db.DbContract.GENRES;
import static pack.cpserver.db.DbContract.Genres;

public class DbBackend implements Closeable {
    private final SQLiteOpenHelper openHelper;

    public DbBackend(SQLiteOpenHelper dbOpenHelper) {
        this.openHelper = dbOpenHelper;
    }

    @CheckResult
    public boolean insertArtists(Stream<Artist> artists) {
        SQLiteDatabase database = openHelper.getWritableDatabase();
        database.beginTransactionNonExclusive();

        boolean success = artists.every(artist -> insertArtist(database, artist));

        success &= artists.flatMap(Artist::genres)
                .collect(ToSolidSet.toSolidSet())
                .every(genre -> insertGenre(database, genre));
        SolidMap<String, Integer> genreIds = getGenreIds(database);

        success &= artists.every(artist -> insertArtistGenres(database, artist, genreIds));

        if (success) {
            database.setTransactionSuccessful();
        }
        database.endTransaction();
        return success;
    }

    @NonNull
    @CheckResult
    public Cursor getArtists(String[] projection,
                             String selection, String[] selectionArgs,
                             String having,
                             String sortOrder) {
        SQLiteDatabase database = openHelper.getReadableDatabase();
        return database.query(ARTISTS_WITH_GENRES,
                projection, selection, selectionArgs, null, having, sortOrder);
    }

    @CheckResult
    boolean insertArtist(SQLiteDatabase database, Artist artist) {
        ContentValues values = new ContentValues();
        values.put(Artists._ID, artist.id());
        values.put(Artists.NAME, artist.name());
        values.put(Artists.TRACKS, artist.tracks());
        values.put(Artists.ALBUMS, artist.albums());
        values.put(Artists.LINK, artist.link());
        values.put(Artists.DESCRIPTION, artist.description());
        values.put(Artists.SMALL_COVER, artist.cover().small());
        values.put(Artists.BIG_COVER, artist.cover().big());
        return database.insert(ARTISTS, null, values) != -1;
    }

    @CheckResult
    boolean insertGenre(SQLiteDatabase database, String genre) {
        ContentValues values = new ContentValues();
        values.put(Genres.NAME, genre);
        return database.insertWithOnConflict(GENRES, null, values,
                SQLiteDatabase.CONFLICT_IGNORE) != -1;
    }

    @NonNull
    SolidMap<String, Integer> getGenreIds(SQLiteDatabase database) {
        try (Cursor cursor =
                     database.query(DbContract.GENRES, null, null, null, null, null, null)) {
            List<Pair<String, Integer>> genreList = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                genreList.add(new Pair<>(cursor.getString(1), cursor.getInt(0)));
            }
            return new SolidList<>(genreList)
                    .collect(ToSolidMap.toSolidMap(pair -> pair.first, pair -> pair.second));
        }
    }

    @CheckResult
    boolean insertArtistGenres(SQLiteDatabase database,
                               Artist artist, SolidMap<String, Integer> genreIds) {
        boolean success = true;
        ContentValues values = new ContentValues();
        values.put(DbContract.ArtistsGenres.ARTISTS_ID, artist.id());
        for (String genre : artist.genres()) {
            values.put(DbContract.ArtistsGenres.GENRES_ID, genreIds.get(genre));
            success &= database.insert(DbContract.ARTISTS_GENRES, null, values) != -1;
        }
        return success;
    }

    @Override
    public void close() throws IOException {
        openHelper.close();
    }
}
