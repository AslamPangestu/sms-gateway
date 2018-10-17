package dev.karim.perumahan.models;

/**
 * Created by mvryan on 17/10/18.
 */

public class Response {
    private Message message;

    public Message getMessage ()
    {
        return message;
    }

    public void setMessage (Message message)
    {
        this.message = message;
    }
}
