package demo

import grails.gorm.transactions.Rollback
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

        when:
        Book book = createBook('FooBarBazQux', 'Matthew')
//        Book book = bookDataService.save('FooBarBazQux', 'Matthew')

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
