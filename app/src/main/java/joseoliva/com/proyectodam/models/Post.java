package joseoliva.com.proyectodam.models;

public class Post {

    private String id;
    private String lugar;
    private String descripcion;
    private String image1;
    private String image2;
    private String region;
    private String idUser;
    private long timestamp;

    public Post(){}

    public Post(String id, String lugar, String descripcion, String image1, String image2, String region, String idUser, long timestamp) {
        this.id = id;
        this.lugar = lugar;
        this.descripcion = descripcion;
        this.image1 = image1;
        this.image2 = image2;
        this.region = region;
        this.idUser = idUser;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
