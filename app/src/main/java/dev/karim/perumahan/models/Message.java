package dev.karim.perumahan.models;

/**
 * Created by mvryan on 17/10/18.
 */

public class Message {
    private String to;

    private String balance;

    private String text;

    private String status;

    public String getTo ()
    {
        return to;
    }

    public void setTo (String to)
    {
        this.to = to;
    }

    public String getBalance ()
    {
        return balance;
    }

    public void setBalance (String balance)
    {
        this.balance = balance;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }
}
