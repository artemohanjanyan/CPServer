package pack.cpserver.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import pack.cpserver.BuildConfig;
import solid.collectors.ToSolidList;
import solid.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DbBackendTest {

    private SQLiteDatabase database;
    private DbBackend backend;
    private Random random = new Random(1234);

    @Before
    public void setUp() throws Exception {
        SQLiteOpenHelper openHelper = new DbOpenHelper(RuntimeEnvironment.application);
        database = openHelper.getWritableDatabase();
        backend = new DbBackend(openHelper);
    }

    @After
    public void tearDown() throws Exception {
        backend.close();
    }

    @Test
    public void insertAndGetSingleArtist() throws Exception {
        Artist artist = artistTestBuilder(0).build();
        assertTrue(backend.insertArtist(database, artist));
        Stream.stream(artist.genres()).forEach(genre ->
                assertTrue(backend.insertGenre(database, genre)));
        assertTrue(backend.insertArtistGenres(database, artist, backend.getGenreIds(database)));

        Cursor artistCursor = backend.getArtists(null, null, null, null, null);
        assertEquals(artistCursor.getCount(), 1);
        assertTrue(artistCursor.moveToNext());
        Artist artist1 = Artist.create(artistCursor);
        assertEquals(artist, artist1);
    }

    @Test
    public void insertAndGetManyArtists() throws Exception {
        List<Artist> artists = new ArrayList<>();

        final int ARTISTS_N = 500;
        for (int i = 0; i < ARTISTS_N; ++i) {
            Artist.Builder builder = artistTestBuilder(i);
            artists.add(builder.build());
        }

        assertTrue(backend.insertArtists(Stream.stream(artists)));

        Cursor artistsCursor = backend.getArtists(null, null, null, null, null);
        List<Artist> artists1 = new ArrayList<>(artistsCursor.getCount());
        while (artistsCursor.moveToNext()) {
            artists1.add(Artist.create(artistsCursor));
        }

        assertEquals(artists.size(), artists1.size());
        List<Artist> sorted = Stream.stream(artists)
                .sort((a1, a2) -> Integer.compare(a1.id(), a2.id()))
                .collect(ToSolidList.toSolidList());
        List<Artist> sorted1 = Stream.stream(artists1)
                .sort((a1, a2) -> Integer.compare(a1.id(), a2.id()))
                .collect(ToSolidList.toSolidList());
        assertEquals(sorted, sorted1);
    }

    @NonNull
    private Artist.Builder artistTestBuilder(int i) {
        Artist.Builder builder = Artist.builder();

        builder.id(i);
        builder.name("artist" + i);

        List<String> genres = new ArrayList<>();
        final int GENRES_N = 50;
        for (int j = 0; j < GENRES_N; ++j) {
            if (random.nextBoolean()) {
                genres.add("genre" + j);
            }
        }
        builder.genres(new HashSet<>(genres));

        builder.tracks(random.nextInt());
        builder.albums(random.nextInt());
        builder.link("link" + i);
        builder.description("description" + i);

        Artist.Cover.Builder coverBuilder = Artist.Cover.builder();
        coverBuilder.small("smallCover" + i);
        coverBuilder.big("bigCover" + i);
        builder.cover(coverBuilder.build());

        return builder;
    }
}
