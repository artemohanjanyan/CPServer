package pack.cpserver.cp;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import pack.cpserver.R;
import pack.cpserver.db.AdapterFactory;
import pack.cpserver.db.Artist;
import pack.cpserver.db.DbBackend;
import pack.cpserver.db.DbOpenHelper;
import solid.stream.Stream;

import static pack.cpserver.cp.ContentContract.ARTISTS;
import static pack.cpserver.cp.ContentContract.AUTHORITY;
import static pack.cpserver.cp.ContentContract.Artists;

public class ContentProvider extends android.content.ContentProvider {
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ARTISTS_CODE = 1;
    private static final int ARTIST_CODE = 2;

    static {
        uriMatcher.addURI(AUTHORITY, ARTISTS, ARTISTS_CODE);
        uriMatcher.addURI(AUTHORITY, ARTISTS + "/#", ARTIST_CODE);
    }

    private DbBackend dbBackend;

    static boolean init(DbBackend backend, Context context) {
        Log.d("qq", "init");
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(AdapterFactory.create())
                .create();
        Type type = new TypeToken<List<Artist>>() {
        }.getType();
        List<Artist> artists = gson.fromJson(new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.artists))), type);
        return backend.insertArtists(Stream.stream(artists));
    }

    @Override
    public boolean onCreate() {
        dbBackend = new DbBackend(new DbOpenHelper(getContext()));
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {

        if (dbBackend.getArtists(null, null, null, null, null).getCount() == 0) {
            Log.d("qq", "before");
            if (!init(dbBackend, getContext())) {
                return null;
            }
        }

        switch (uriMatcher.match(uri)) {
            case ARTISTS_CODE:
                return dbBackend.getArtists(projection, selection, selectionArgs, null, sortOrder);
            case ARTIST_CODE:
                return dbBackend.getArtists(projection, selection, selectionArgs,
                        Artists._ID + "=" + uri.getLastPathSegment(), sortOrder);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri,
                      ContentValues values,
                      String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
