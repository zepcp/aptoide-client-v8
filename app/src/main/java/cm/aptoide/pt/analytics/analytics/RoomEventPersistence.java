package cm.aptoide.pt.analytics.analytics;

import cm.aptoide.analytics.implementation.EventsPersistence;
import cm.aptoide.analytics.implementation.data.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 12/01/2018.
 */

public class RoomEventPersistence implements EventsPersistence {
  private final EventDbAccessObject eventDAO;
  private final RoomEventMapper mapper;

  public RoomEventPersistence(EventDbAccessObject eventDAO, RoomEventMapper mapper) {
    this.eventDAO = eventDAO;
    this.mapper = mapper;
  }

  @Override public Completable save(Event event) {
    return Completable.fromEmitter(completableEmitter -> {
      try {
        eventDAO.insert(mapper.map(event));
        completableEmitter.onCompleted();
      } catch (JsonProcessingException e) {
        completableEmitter.onError(e);
      }
    });
  }

  @Override public Completable save(List<Event> events) {
    return Observable.from(events)
        .flatMapCompletable(event -> save(event))
        .toList()
        .toCompletable();
  }

  @Override public Observable<List<Event>> getAll() {
    return eventDAO.getAll()
        .flatMap(roomEvents -> {
          try {
            return Observable.just(mapper.map(roomEvents));
          } catch (IOException e) {
            return Observable.error(e);
          }
        });
  }

  @Override public Completable remove(List<Event> events) {
    return Observable.from(events)
        .flatMap(event -> {
          try {
            return Observable.just(mapper.map(event));
          } catch (JsonProcessingException e) {
            return Observable.error(e);
          }
        })
        .doOnNext(roomEvent -> eventDAO.delete(roomEvent))
        .toList()
        .toCompletable();
  }
}
