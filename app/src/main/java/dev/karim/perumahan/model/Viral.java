package dev.karim.perumahan.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.net.URL;
import java.util.List;

@Root(name="response")
public class Viral {

    @Element(name="message", required=false)
    Message message;

    public Message getMessage() {return this.message;}
    public void setMessage(Message value) {this.message = value;}

    public static class Message {

        @Element(name="balance", required=false)
        String balance;

        @Element(name="to", required=false)
        String to;

        @Element(name="text", required=false)
        String text;

        @Element(name="status", required=false)
        String status;

        public String getBalance() {return this.balance;}
        public void setBalance(String value) {this.balance = value;}

        public String getTo() {return this.to;}
        public void setTo(String value) {this.to = value;}

        public String getText() {return this.text;}
        public void setText(String value) {this.text = value;}

        public String getStatus() {return this.status;}
        public void setStatus(String value) {this.status = value;}

    }

}