package pack.cpserver.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import pack.cpserver.BuildConfig;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DbOpenHelperTest {

    @Test
    public void onCreate() throws Exception {
        SQLiteOpenHelper openHelper = new DbOpenHelper(RuntimeEnvironment.application);
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        try (Cursor cursor =
                     sqLiteDatabase.query("sqlite_master", null, null, null, null, null, null)) {
            assertTrue(cursor.getCount() >= 3);
        }
    }

    @Test
    public void onUpgrade() throws Exception {
        SQLiteOpenHelper openHelper = new DbOpenHelper(RuntimeEnvironment.application);
        openHelper.onUpgrade(openHelper.getWritableDatabase(), 0, 0);
        onCreate();
    }
}