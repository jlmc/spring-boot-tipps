package io.github.jlmc.uploadcsv.adapters.out.persistence;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.github.jlmc.uploadcsv.domain.Location;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync;
import io.mongock.driver.mongodb.reactive.util.SubscriberSync;
import org.bson.Document;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@ChangeUnit(
        id = "locations-update-open-hours-date-to-iso-local-time",
        order = "1"
)
public class UpdateLocationsOpenAndCloseAtToIsoLocalTime {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateLocationsOpenAndCloseAtToIsoLocalTime.class);

    private static String toIsoLocalTimeString(Date openAtLegacy) {
        ZonedDateTime utc = openAtLegacy.toInstant().atZone(ZoneId.of("UTC"));
        return utc.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    private static Document getQueryById(Document document) {
        return new Document("_id", document.get("_id"));
    }

    @Execution
    public void execution(MongoDatabase mongoDatabase,
                          ReactiveMongoTemplate reactiveMongoTemplate) {
        LOGGER.info("===> Running Migrations");
        SubscriberSync<Document> subscriber = new MongoSubscriberSync<>();
        MongoCollection<Document> locationCollection = mongoDatabase.getCollection(Location.LOCATIONS, Document.class);
        locationCollection.find().subscribe(subscriber);


        subscriber.get()
                  .stream()
                  .map(this::updateLocationDocument)
                  .map(document -> {
                      SubscriberSync<UpdateResult> updateSubscriber = new MongoSubscriberSync<>();
                      Publisher<UpdateResult> updateResultPublisher = locationCollection.replaceOne(getQueryById(document), document);
                      updateResultPublisher.subscribe(updateSubscriber);

                      return updateSubscriber.getFirst();
                  })
                  .forEach(result -> LOGGER.info("result[upsertId:{}, matches: {}, modifies: {}, acknowledged: {}]",
                          result.getUpsertedId(),
                          result.getMatchedCount(),
                          result.getModifiedCount(),
                          result.wasAcknowledged()));
    }

    private Document updateLocationDocument(Document location) {

        @SuppressWarnings("unchecked")
        Map<DayOfWeek, List<Document>> openHours = location.get("openHours", Map.class);

        openHours.values()
                 .stream()
                 .flatMap(Collection::stream)
                 .forEach((Document openPeriodDocument) -> {
                     Object openAt = openPeriodDocument.get("openAt", Object.class);
                     if (openAt instanceof Date openAtLegacy) {
                         String isoLocalTimeString = toIsoLocalTimeString(openAtLegacy);
                         openPeriodDocument.put("openAt", isoLocalTimeString);
                     }

                     Object closeAt = openPeriodDocument.get("closeAt", Object.class);
                     if (closeAt instanceof Date closeAtLegacy) {
                         String isoLocalTimeString = toIsoLocalTimeString(closeAtLegacy);
                         openPeriodDocument.put("closeAt", isoLocalTimeString);
                     }
                 });

        return location;
    }

    @RollbackExecution
    public void rollbackExecution(MongoDatabase mongoDatabase) {
        LOGGER.info("Executing rollback!");
    }
}
