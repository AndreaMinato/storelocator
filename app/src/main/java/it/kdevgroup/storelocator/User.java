package it.kdevgroup.storelocator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by damiano on 15/04/16.
 */
public class User implements Parcelable {
    public static final String ID_KEY = "_id";
    //    public static final String PASSWORD_KEY = "password";
    public static final String SESSION_KEY = "session";
    public static final String SESSION_TTL_KEY = "session_ttl";
    public static final String NAME_KEY = "name";
    public static final String SURNAME_KEY = "surname";
    public static final String EMAIL_KEY = "email";

    private String id;
    //    private String password;
    private String session;
    private Integer sessionTtl;
    private String name;
    private String surname;
    private String email;

    public User() {
    }

    public User(String id, String session, Integer sessionTtl, String name, String surname, String email) {
        this.id = id;
        this.session = session;
        this.sessionTtl = sessionTtl;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public User(Map<String, Object> map) {
        id = map.get("id").toString(); //non posso passargli ID_KEY
        session = map.get(SESSION_KEY).toString(); //qui per fortuna sì
        sessionTtl = (Integer) map.get("sessionTtl");
        name = map.get(NAME_KEY).toString();
        surname = map.get(SURNAME_KEY).toString();
        email = map.get(EMAIL_KEY).toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Integer getSessionTtl() {
        return sessionTtl;
    }

    public void setSessionTtl(Integer sessionTtl) {
        this.sessionTtl = sessionTtl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Determina se la sessione dell'utente è scaduta
     *
     * @return <b>true</b> se è scaduta<br><b>false</b> se è ancora attiva
     */
    public boolean isSessionExpired() {
//        Log.d("isSessionExpired", new SimpleDateFormat("dd MM yyyy").format(new Date()));
//        Log.d("isSessionExpired", new SimpleDateFormat("dd MM yyyy").format(new Date((long) sessionTtl * 1000)));
        return new Date().after(new Date((long) sessionTtl * 1000));
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("user", this);

        return hashMap;
    }

    // parcellizzazione

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(session);
        dest.writeInt(sessionTtl);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(email);
    }

    public static final Parcelable.Creator<User> CREATOR = new ClassLoaderCreator<User>() {
        @Override
        public User createFromParcel(Parcel source, ClassLoader loader) {
            return new User(source);
        }

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        id = in.readString();
        session = in.readString();
        sessionTtl = in.readInt();
        name = in.readString();
        surname = in.readString();
        email = in.readString();
    }
}
