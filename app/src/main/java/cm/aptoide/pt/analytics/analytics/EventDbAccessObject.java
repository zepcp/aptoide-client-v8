package cm.aptoide.pt.analytics.analytics;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import cm.aptoide.pt.database.realm.RoomEvent;
import java.util.List;
import rx.Observable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao public interface EventDbAccessObject {

  @Query("SELECT * FROM event")
  Observable<List<RoomEvent>> getAll(); //Flowable with RxJava2; Maybe for specific(filter) queries; Single for queries that return only one

  /*
  @Query("SELECT * FROM user where first_name LIKE  :firstName AND last_name LIKE :lastName")
  User findByName(String firstName, String lastName); */ // example of a select by query

  @Insert void insertAll(RoomEvent... roomEvents);

  @Insert(onConflict = REPLACE) void insert(RoomEvent roomEvent); //5 more policies

  @Delete void delete(RoomEvent roomEvent);
}
