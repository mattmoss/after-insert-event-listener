package demo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Book {
    String title
    String author

    static constraints = {
        title nullable: false
        author nullable: true
    }
}
