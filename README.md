# Project Gutenberg Database

### About

	Project Gutenberg Word Count System 

### Design considerations

### Schedule
	2021-08-30 through 2021-09-02

### Project Scope

#### Requirements

	Create a database from the documents in the Project Gutenberg.
	Create an interface which allows us to get the word count for each document, either searching by word or document Id.
	Should include limit flag.

### Project development

#### File transmission

	Entire catalog of books in Gutenberg can be downloaded from https://gutenberg.org/cache/epub/feeds/rdf-files.tar.bz2
	This system handles file transmission and uncompress it to extract a few key information of all documents.
	This system handles transmisson of individual documents, if found, to extract word count data. (ETL) 

#### Database

	PostgreSQL
	Automated configuration through JPA Hibernate
	DDL included for reference

#### Beam (distributed) processing

	Populate book_info and book_info_detail (word count) via Apache Beam pipelines
	The system goes through extracted rdf files to generate book_info data
	and put automatically downloaded documents through to generate word count details

#### Search processing
	
	It search documents by word and words by documents.
	When searching words by documents, you can either search by document id, title, or author.

#### Tech stack

	Java
	Spring Boot
	Gradle
	JPA/Hibernate
	PostgreSQL
	Apache Beam

#### Unusual library

	SPARQL is a query language and a protocol for accessing RDF designed by the W3C RDF Data Access Working Group.
	org.apache.jena:jena-core:4.1.0
	org.apache.jena:jena-arq:4.1.0

#### Enhancement considerations

	Keep track of search/results to detect search patterns
	Auto generate word search pattern (e.g. if pattern of searching two words one after the other is detected, store them as associated words. Requires rest endpoint detecting ip address or something similar)
