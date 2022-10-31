package demo

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Audit {
    String event
    Book book

    static constraints = {
        event nullable: false, blank: false
        book nullable: false
    }

    static mapping = {
        book lazy: false
    }
}