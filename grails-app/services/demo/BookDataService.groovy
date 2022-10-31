package demo

import grails.gorm.services.Service
import groovy.transform.CompileStatic

// GORM Data Services are transactional by default: no annotation needed.
@CompileStatic
@Service(Book)
interface BookDataService {
    List<Book> findAll()
    Book save(String title, String author)
}
