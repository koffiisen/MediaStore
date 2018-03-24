package dev.koffi.mediastore;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Loader for list of tracks.
 */
class MusicLoader {

    private final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    private Context context;

    public MusicLoader(Context context) {
        this.context = context;
    }


    public Collection<MusicItem> loadInBackground() {
        String[] projection = new String[]{
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
        };
        Cursor cursor = this.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Audio.Media.IS_MUSIC + "=1",
                null,
                "LOWER(" + MediaStore.Audio.Media.ARTIST + ") ASC, " +
                        "LOWER(" + MediaStore.Audio.Media.ALBUM + ") ASC, " +
                        "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC"
        );
        if (cursor == null) {
            return Collections.emptyList();
        }
        List<MusicItem> items = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                do {
                    MusicItem item = new MusicItem()
                            .title(cursor.getString(title))
                            .album(cursor.getString(album))
                            .artist(cursor.getString(artist))
                            .duration(cursor.getLong(duration))
                            .albumArtUri(ContentUris.withAppendedId(albumArtUri, cursor.getLong(albumId)))
                            .fileUri(Uri.parse(cursor.getString(data)));
                    item.albumArtUri(Uri.parse(getRealPathFromUri(context, item.albumArtUri())));
                    items.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
