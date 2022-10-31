package demo

import grails.events.annotation.Subscriber
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PostInsertEvent

@Slf4j
@CompileStatic
class AuditListenerService {
    AuditDataService auditDataService

    @Subscriber
    void afterInsert(PostInsertEvent event) {
        if (event.entityObject instanceof Book) {
            log.info 'After book save...'

            // As in the AuditListenerServiceSpec, any of the means of creation below will successfully persist
            // an Audit object, and the means are ordered preferentially.
            Audit audit = auditDataService.save('Book inserted', (Book) event.entityObject)        // 1
            // Audit audit = createAudit('Book inserted', (Book) event.entityObject)                     // 2
            // Audit audit = Audit.withTransaction {                                                     // 3
            //     new Audit(event: 'Book inserted', book: (Book) event.entityObject).save()
            // }
             if (!audit) {
                log.error 'Audit failed to save'
             }
        }
    }

    @Transactional
    Audit createAudit(String event, Book book) {
        new Audit(event: event, book: book).save()
    }

    Long bookId(AbstractPersistenceEvent event) {
        if (event.entityObject instanceof Book) {
            return ((Book) event.entityObject).id
        }
    }
}
