package com.teamtreehouse.ribbit.models;


import com.teamtreehouse.ribbit.mockdata.MockMessages;
import com.teamtreehouse.ribbit.models.callbacks.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Message implements Comparable<Message> {

    //public static final String CLASS_MESSAGES = "Messages";

    // Field names
    public static final String KEY_RECIPIENT_IDS = "recipientIds";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE = "file";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_CREATED_AT = "createdAt";

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";

    private UUID id;
    private Date createdAt;
    private MessageFile messageFile;
    private HashMap<String, Object> map;

    public Message(String className) {
        id = UUID.randomUUID();
        createdAt = new Date();
        map = new HashMap<>();
    }

    public void put(String key, Object o) {
        map.put(key, o);
    }

    public UUID getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getString(String key) {
        return (String)map.get(key);
    }

    public MessageFile getFile(String key) {
        return (MessageFile)map.get(key);
    }

    public List<String> getList(String key) {
        if (key.equals(KEY_RECIPIENT_IDS)) {
            return (List<String>) map.get(KEY_RECIPIENT_IDS);
        }
        return null;
    }

    public void deleteInBackground() {
        MockMessages.getInstance().deleteMessage(id);
    }

    public void removeRecipient(String idToRemove) {
        ArrayList<String> recipients = (ArrayList<String>) map.get(KEY_RECIPIENT_IDS);
        recipients.remove(idToRemove);
        map.put(KEY_RECIPIENT_IDS, recipients);
    }

    public void saveInBackground(SaveCallback callback) {
        MockMessages.getInstance    ().saveMessage(this);
        callback.done(null);
    }

    @Override
    public int compareTo(Message another) {
        Date otherCreatedAt = ((Message)another).getCreatedAt();
        return this.createdAt.compareTo(otherCreatedAt);
    }

    public static Query<Message> getQuery() {
        Query<Message> query = new Query<Message>(Message.class.getSimpleName());
        query.setDataSet(MockMessages.getInstance().getMessagesForUser(User.getCurrentUser().getObjectId()));
        return query;
    }
}
