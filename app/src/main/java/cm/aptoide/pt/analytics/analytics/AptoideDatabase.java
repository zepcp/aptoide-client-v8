package cm.aptoide.pt.analytics.analytics;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import cm.aptoide.pt.database.realm.RoomEvent;
import cm.aptoide.pt.deprecated.SQLiteDatabaseHelper;

import static cm.aptoide.pt.analytics.analytics.AptoideDatabase.VERSION;

//entities = list of DB objects
@Database(entities = {RoomEvent.class}, version = VERSION) public abstract class AptoideDatabase
    extends RoomDatabase {

  static final int VERSION = 1;

  private static volatile AptoideDatabase INSTANCE;

  public static AptoideDatabase getAptoideDatabase(
      Context context) { //Not needed due to the use of Dagger
    if (INSTANCE == null) {
      //Needs verification to not do in mainThread
      synchronized (AptoideDatabase.class) {
        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AptoideDatabase.class,
            SQLiteDatabaseHelper.DATABASE_NAME)
            //.addMigrations()
            .build();
      }
    }
    return INSTANCE;
  }

  public static void destroyInstance() {
    INSTANCE = null;
  }

  public abstract EventDbAccessObject eventDbAccessObject(); //This class needs to have an abstract of all DAO objects
}
