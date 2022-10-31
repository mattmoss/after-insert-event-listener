package demo

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@Integration
class AuditListenerServiceSpec extends Specification {

    BookDataService bookDataService
    AuditDataService auditDataService

    void 'book.save triggers auditDataService.save'() {
        setup:
        def conditions = new PollingConditions(timeout: 30)

        // Any of the means of creation below (i.e. 1, 2, or 3) will successfully create a Book
        // in a transaction and, asynchronously (b/c @Subscriber) will create the Audit object in
        // the listener. (PollingConditions is used b/c asynchronous.)
        //
        // The order of options 1, 2, and 3 is the preferred order. That is, data services are
        // preferred over @Transactional methods, which is preferred over withTransaction.
        when:
        Book book = bookDataService.save('FooBarBazQux', 'Matthew')     // 1
        // Book book = createBook('FooBarBazQux', 'Matthew')                       // 2
        // Book book = Book.withTransaction {                                      // 3
        //     new Book(title: 'FooBarBazQux', author: 'Matthew').save()
        // }

        then:
        book
        book.id
        conditions.eventually {
            assert auditDataService.count() == old(auditDataService.count()) + 1
        }

        when:
        Audit lastAudit = this.lastAudit()

        then:
        lastAudit.event == 'Book inserted'
        lastAudit.book.ident() == book.ident()

        cleanup:
        auditDataService.deleteByBook(book)
    }

    @Transactional
    Book createBook(String title, String author) {
        new Book(title: title, author: author).save()
    }

    Audit lastAudit() {
        int offset = Math.max(((auditDataService.count() as int) - 1), 0)
        auditDataService.findAll([max: 1, offset: offset]).first()
    }
}
