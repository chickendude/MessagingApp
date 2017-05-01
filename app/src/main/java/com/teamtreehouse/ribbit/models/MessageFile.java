package com.teamtreehouse.ribbit.models;

import android.net.Uri;

/**
 * Created by benjakuben on 10/12/16.
 */

public class MessageFile {
    private String fileName;
    private byte[] fileBytes;
    private Uri uri;

    public MessageFile(String fileName, byte[] fileBytes, Uri uri) {
        this.fileName = fileName;
        this.fileBytes = fileBytes;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
