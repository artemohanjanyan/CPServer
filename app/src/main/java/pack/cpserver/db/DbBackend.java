package pack.cpserver.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

import static pack.cpserver.db.DbContract.*;

public class DbBackend implements Closeable {
    private final SQLiteOpenHelper openHelper;

    public DbBackend(SQLiteOpenHelper dbOpenHelper) {
        this.openHelper = dbOpenHelper;
    }

    public void insertArtists(Stream<Artist> artists) {
        SQLiteDatabase database = openHelper.getWritableDatabase();
        database.beginTransaction();

        artists.forEach(artist -> insertArtist(database, artist));

        artists.flatMap(Artist::genres)
                .collect(ToSolidSet.toSolidSet())
                .forEach(genre -> insertGenre(database, genre));
        SolidMap<String, Integer> genreIds = getGenreIds(database);

        artists.forEach(artist -> insertArtistGenres(database, artist, genreIds));

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public Cursor getArtists() {
        SQLiteDatabase database = openHelper.getReadableDatabase();
        String select =
                String.format("select %s, %s, group_concat(%s, '%s'), %s, %s, %s, %s, %s, %s ",
                        ARTISTS + "." + Artists.ID,
                        ARTISTS + "." + Artists.NAME,
                        GENRES + "." + Genres.NAME, GENRES_JOIN_DELIMITER,
                        Artists.TRACKS,
                        Artists.ALBUMS,
                        Artists.LINK,
                        Artists.DESCRIPTION,
                        Artists.SMALL_COVER,
                        Artists.BIG_COVER);

        String from =
                String.format("from %s left join %s on %s = %s left join %s on %s = %s group by %s",
                        ARTISTS,
                        ARTISTS_GENRES, ARTISTS + "." + Artists.ID, ArtistsGenres.ARTISTS_ID,
                        GENRES, GENRES + "." + Genres.ID, ArtistsGenres.GENRES_ID,
                        ARTISTS + "." + Artists.ID);

        return database.rawQuery(select + from, null);
    }

    public long insertArtist(SQLiteDatabase database, Artist artist) {
        ContentValues values = new ContentValues();
        values.put(Artists.ID, artist.id());
        values.put(Artists.NAME, artist.name());
        values.put(Artists.TRACKS, artist.tracks());
        values.put(Artists.ALBUMS, artist.albums());
        values.put(Artists.LINK, artist.link());
        values.put(Artists.DESCRIPTION, artist.description());
        values.put(Artists.SMALL_COVER, artist.cover().small());
        values.put(Artists.BIG_COVER, artist.cover().big());
        return database.insert(ARTISTS, null, values);
    }

    public long insertGenre(SQLiteDatabase database, String genre) {
        ContentValues values = new ContentValues();
        values.put(Genres.NAME, genre);
        return database.insertWithOnConflict(GENRES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public SolidMap<String, Integer> getGenreIds(SQLiteDatabase database) {
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

    public void insertArtistGenres(SQLiteDatabase database,
                                    Artist artist, SolidMap<String, Integer> genreIds) {
        ContentValues values = new ContentValues();
        values.put(DbContract.ArtistsGenres.ARTISTS_ID, artist.id());
        for (String genre : artist.genres()) {
            values.put(DbContract.ArtistsGenres.GENRES_ID, genreIds.get(genre));
            database.insert(DbContract.ARTISTS_GENRES, null, values);
        }
    }

    @Override
    public void close() throws IOException {
        openHelper.close();
    }
}
