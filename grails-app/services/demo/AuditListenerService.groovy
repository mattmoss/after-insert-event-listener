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
    @Transactional
    void afterInsert(PostInsertEvent event) {
        if (event.entityObject instanceof Book) {
            log.info 'After book save...'
            Audit audit = new Audit(event: 'Book inserted', book: (Book) event.entityObject)
            if (!audit.save()) {
                log.error 'Audit failed to save'
            } else {
                log.info 'Audit event saved'
            }
//            auditDataService.save('Book inserted', (Book) event.entityObject)
        }
    }

    Long bookId(AbstractPersistenceEvent event) {
        if (event.entityObject instanceof Book) {
            return ((Book) event.entityObject).id
        }
    }
}
