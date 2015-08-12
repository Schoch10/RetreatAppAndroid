package slalom.com.retreatapplication.db;

/**
 * Created by senthilrajav on 8/10/15.
 */
public class CheckIn {
    private long id;
    private long checkIns;
    private String location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(long checkIns) {
        this.checkIns = checkIns;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
