package joseoliva.com.proyectodam.models;

public class Comentario {

    private String id;
    private String comentario;
    private String idUser;
    private String idPost;
    long timestamp;

    public Comentario(){}

    public Comentario(String id, String comentario, String idUser, String idPost, long timestamp) {
        this.id = id;
        this.comentario = comentario;
        this.idUser = idUser;
        this.idPost = idPost;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
