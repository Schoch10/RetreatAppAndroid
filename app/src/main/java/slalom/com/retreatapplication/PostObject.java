package slalom.com.retreatapplication;

/**
 * Created by alexanderp on 8/14/2015.
 */
public class PostObject {
    private Integer postId;
    private Integer userId;
    private String userName;
    private Integer locationId;
    private String image;
    private String text;
    private Integer timestamp;


    public PostObject(Integer postId, Integer userId, String userName, Integer locationId, String image, String text, Integer timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.locationId = locationId;
        this.image = image;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Integer postId() {return this.postId;}
    public Integer userId() {return this.userId;}
    public String userName() {return this.userName;}
    public Integer locationId() {return this.locationId;}
    public String image() {return this.image;}
    public String text() {return this.text;}
    public Integer timestamp() {return this.timestamp;}
}