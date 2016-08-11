package pack.cpserver.db;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.ryanharter.auto.value.parcel.ParcelAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static pack.cpserver.db.DbContract.GENRES_JOIN_DELIMITER;

@AutoValue
public abstract class Artist implements Parcelable {
    public static Artist create(Cursor cursor) {
        Artist.Builder builder = new $$AutoValue_Artist.Builder();
        builder.id(cursor.getInt(0));
        builder.name(cursor.getString(1));
        String genres = cursor.getString(2);
        if (genres == null) {
            genres = "";
        }
        builder.genres(new HashSet<>(Arrays.asList(genres
                .split(Pattern.quote(GENRES_JOIN_DELIMITER)))));
        builder.tracks(cursor.getInt(3));
        builder.albums(cursor.getInt(4));
        String string = cursor.getString(5);
        builder.link(string);
        builder.description(cursor.getString(6));

        Cover.Builder coverBuilder = new $$AutoValue_Artist_Cover.Builder();
        coverBuilder.small(cursor.getString(7));
        coverBuilder.big(cursor.getString(8));
        builder.cover(coverBuilder.build());

        return builder.build();
    }

    public static com.google.gson.TypeAdapter<Artist> typeAdapter(Gson gson) {
        return new $AutoValue_Artist.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new $$AutoValue_Artist.Builder();
    }

    public abstract int id();

    public abstract String name();

    @ParcelAdapter(GenresTypeAdapter.class)
    public abstract Set<String> genres();

    @Nullable
    public abstract Integer tracks();

    @Nullable
    public abstract Integer albums();

    @Nullable
    public abstract String link();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract Cover cover();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int id);

        public abstract Builder name(String name);

        public abstract Builder genres(Set<String> genres);

        public abstract Builder tracks(Integer tracks);

        public abstract Builder albums(Integer albums);

        public abstract Builder link(String link);

        public abstract Builder description(String description);

        public abstract Builder cover(Cover cover);

        public abstract Artist build();
    }

    @AutoValue
    public static abstract class Cover implements Parcelable {
        public static com.google.gson.TypeAdapter<Cover> typeAdapter(Gson gson) {
            return new $AutoValue_Artist_Cover.GsonTypeAdapter(gson);
        }

        public static Builder builder() {
            return new $$AutoValue_Artist_Cover.Builder();
        }

        @Nullable
        public abstract String small();

        @Nullable
        public abstract String big();

        @AutoValue.Builder
        public abstract static class Builder {
            public abstract Builder small(String small);

            public abstract Builder big(String big);

            public abstract Cover build();
        }
    }

    public static class GenresTypeAdapter
            implements com.ryanharter.auto.value.parcel.TypeAdapter<Set<String>> {

        @Override
        public Set<String> fromParcel(Parcel in) {
            int n = in.readInt();
            Set<String> strings = new HashSet<>();
            for (int i = 0; i < n; ++i) {
                strings.add(in.readString());
            }
            return strings;
        }

        @Override
        public void toParcel(Set<String> value, Parcel dest) {
            dest.writeInt(value.size());
            for (String string : value) {
                dest.writeString(string);
            }
        }
    }
}
