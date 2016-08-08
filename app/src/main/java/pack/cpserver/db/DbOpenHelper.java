package pack.cpserver.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static pack.cpserver.db.DbContract.ARTISTS;
import static pack.cpserver.db.DbContract.ARTISTS_GENRES;
import static pack.cpserver.db.DbContract.Artists;
import static pack.cpserver.db.DbContract.ArtistsGenres;
import static pack.cpserver.db.DbContract.DB_NAME;
import static pack.cpserver.db.DbContract.GENRES;
import static pack.cpserver.db.DbContract.Genres;

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(this.getClass().getSimpleName(), "db created");

        db.execSQL("CREATE TABLE " + ARTISTS + " (" +
                Artists._ID + " INTEGER PRIMARY KEY," +
                Artists.NAME + " TEXT," +
                Artists.TRACKS + " INTEGER," +
                Artists.ALBUMS + " INTEGER," +
                Artists.LINK + " TEXT," +
                Artists.DESCRIPTION + " TEXT," +
                Artists.SMALL_COVER + " TEXT," +
                Artists.BIG_COVER + " TEXT)");

        db.execSQL("CREATE TABLE " + GENRES + " (" +
                Genres._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Genres.NAME + " TEXT UNIQUE)");

        db.execSQL("CREATE TABLE " + ARTISTS_GENRES + " (" +
                ArtistsGenres.ARTISTS_ID + " INTEGER," +
                ArtistsGenres.GENRES_ID + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + ARTISTS_GENRES);
        onCreate(db);
    }
}
