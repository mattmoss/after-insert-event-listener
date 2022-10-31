package demo

import grails.gorm.services.Service
import groovy.transform.CompileStatic

@CompileStatic
@Service(Book)
interface BookDataService {
    List<Book> findAll()
    Book save(String title, String author)
}
