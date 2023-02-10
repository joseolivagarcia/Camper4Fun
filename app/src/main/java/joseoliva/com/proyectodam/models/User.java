package joseoliva.com.proyectodam.models;

public class User {

    private String id;
    private String email;
    private String username;
    private String model_dron;
    private String imageperfil;
    private String imageportada;
    private long timestamp; //en esta var guardaremos la fecha de cuando se crea el usuario
    private long lastConnection;
    private boolean online;

    public User(){

    }

    public User(String id, String email, String username, String model_dron, String imageperfil, String imageportada, long timestamp, long lastConnection, boolean online) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.model_dron = model_dron;
        this.imageperfil = imageperfil;
        this.imageportada = imageportada;
        this.timestamp = timestamp;
        this.lastConnection = lastConnection;
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getModel_dron() {
        return model_dron;
    }

    public void setModel_dron(String model_dron) {
        this.model_dron = model_dron;
    }

    public String getImageperfil() {
        return imageperfil;
    }

    public void setImageperfil(String imageperfil) {
        this.imageperfil = imageperfil;
    }

    public String getImageportada() {
        return imageportada;
    }

    public void setImageportada(String imageportada) {
        this.imageportada = imageportada;
    }

    public long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(long lastConnection) {
        this.lastConnection = lastConnection;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
