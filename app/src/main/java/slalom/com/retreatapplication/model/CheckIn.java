package slalom.com.retreatapplication.model;

import java.sql.Date;

/**
 * Created by senthilrajav on 8/10/15.
 */
public class CheckIn {
    private Date checkinDate;
    private long locationId;
    private String location;
    private String username;
    private long userId;
    private long checkInID;
    private long checkInLocation;

    public Date getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(Date checkinDate) {
        this.checkinDate = checkinDate;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCheckInID() {
        return checkInID;
    }

    public void setCheckInID(long checkInID) {
        this.checkInID = checkInID;
    }

    public long getCheckInLocation() {
        return checkInLocation;
    }

    public void setCheckInLocation(long checkInLocation) {
        this.checkInLocation = checkInLocation;
    }
}
