package cm.aptoide.pt.database.realm;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "event") public class RoomEvent {

  @PrimaryKey(autoGenerate = true) private long timestamp; //generates ID automatically
  @ColumnInfo(name = "eventName") private String eventName;
  private int action; //default name = variable name
  private String context;
  private String data;

  public RoomEvent(long timestamp, String eventName, int action, String context, String data) {
    this.timestamp = timestamp;
    this.eventName = eventName;
    this.action = action;
    this.context = context;
    this.data = data;
  }

  public RoomEvent() {
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
