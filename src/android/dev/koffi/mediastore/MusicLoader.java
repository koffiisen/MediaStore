package dev.koffi.mediastore;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;

import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.net.URISyntaxException;
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

                    Uri uriget = item.albumArtUri();
                    if (uriget != null) {
                        try {
                            item.coverArtPath(getFilePath(context, uriget));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }


                    items.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return items;
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
