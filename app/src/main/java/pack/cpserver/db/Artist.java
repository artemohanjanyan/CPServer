package pack.cpserver.db;

import android.database.Cursor;

import com.google.auto.value.AutoValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@AutoValue
public abstract class Artist {
    public abstract int id();
    public abstract String name();
    public abstract Set<String> genres();
    public abstract int tracks();
    public abstract int albums();
    public abstract String link();
    public abstract String description();
    public abstract Cover cover();

    public static Artist create(Cursor cursor) {
        Artist.Builder builder = new AutoValue_Artist.Builder();
        builder.id(cursor.getInt(0));
        builder.name(cursor.getString(1));
        builder.genres(new HashSet<>(Arrays.asList(cursor.getString(2)
                .split(Pattern.quote(DbContract.GENRES_JOIN_DELIMITER)))));
        builder.tracks(cursor.getInt(3));
        builder.albums(cursor.getInt(4));
        builder.link(cursor.getString(5));
        builder.description(cursor.getString(6));

        Cover.Builder coverBuilder = new AutoValue_Artist_Cover.Builder();
        coverBuilder.small(cursor.getString(7));
        coverBuilder.big(cursor.getString(8));
        builder.cover(coverBuilder.build());

        return builder.build();
    }

    public static Builder builder() {
        return new AutoValue_Artist.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int id);
        public abstract Builder name(String name);
        public abstract Builder genres(Set<String> genres);
        public abstract Builder tracks(int tracks);
        public abstract Builder albums(int albums);
        public abstract Builder link(String link);
        public abstract Builder description(String description);
        public abstract Builder cover(Cover cover);
        public abstract Artist build();
    }

    @AutoValue
    public static abstract class Cover {
        public abstract String small();
        public abstract String big();

        public static Builder builder() {
            return new AutoValue_Artist_Cover.Builder();
        }

        @AutoValue.Builder
        public abstract static class Builder {
            public abstract Builder small(String small);
            public abstract Builder big(String big);
            public abstract Cover build();
        }
    }
}
