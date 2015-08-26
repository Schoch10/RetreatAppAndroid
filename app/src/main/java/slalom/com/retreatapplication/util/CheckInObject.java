package slalom.com.retreatapplication.util;

/**
 * Created by alexanderp on 8/26/2015.
 */
public class CheckInObject {
    private Integer userId;
    private String userName;
    private Integer locationId;
    private String locationName;
    private Integer checkInId;
    private Long timestamp;

    public CheckInObject(Integer checkInId, Integer userId, String userName, Integer locationId, String locationName, Long timestamp) {
        this.checkInId = checkInId;
        this.userId = userId;
        this.userName = userName;
        this.locationId = locationId;
        this.locationName = locationName;
        this.timestamp = timestamp;

    }

    public Integer userId() {return this.userId;}
    public String userName() {return this.userName;}
    public Integer locationId() {return this.locationId;}
    public String locationName() {return this.locationName;}
    public Integer getCheckInId() {return this.checkInId;}
    public Long getTimestamp() {return this.timestamp;}

    public String toString() {
        return "[{\"checkInId\":"+this.checkInId
                +", \"userId\":"+this.userId
                +", \"userName\":" +this.userName
                +", \"locationId\":"+this.locationId
                +", \"locationName\":"+this.locationName
                +", \"timestamp\":"+this.timestamp
                +"}]";
    }

}