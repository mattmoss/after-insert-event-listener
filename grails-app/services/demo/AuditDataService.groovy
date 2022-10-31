package demo

import grails.gorm.services.Service
import grails.gorm.services.Where
import groovy.transform.CompileStatic

// GORM Data Services are transactional by default: no annotation needed.
@CompileStatic
@Service(Audit)
interface AuditDataService {
    Number count()
    List<Audit> findAll(Map args)
    Audit save(String event, Book book)

    @Where({ book == theBook })
    void deleteByBook(Book theBook)
}
