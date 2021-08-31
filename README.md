# Project Gutenberg Database

### About

	

### Design considerations

### Schedule
	2021-08-29 through 2021-09-02

### Project Scope

#### Requirements

	Create a database from the documents in the Project Gutenberg.
	Create an interface which allows us to get the word count for each document, either searching by word or document Id.
	Should include limit flag.
	(Optional) search words by title or author
	(Optional) creative features
	(Optional) automated setup and configuration

### Project development

#### File transmission

	Entire catalog of books in Gutenberg can be downloaded from https://gutenberg.org/cache/epub/feeds/rdf-files.tar.bz2
	This system handles file transmission and uncompress it to extract a few key information of all documents.
	This system handles transmisson of individual documents, if found, to extract word count data. (ETL) 

#### Database design

	PostgreSQL

#### Batch processing

	Populate database in batches

#### Search processing
	
	how does this app search the entire Gutenberg efficiently

#### Tech stack

	Java
	Spring Boot
	Gradle
	JPA
	PostgreSQL
	Apache Beam

#### Unusual library

	SPARQL is a query language and a protocol for accessing RDF designed by the W3C RDF Data Access Working Group.
	org.apache.jena:jena-core:4.1.0
	org.apache.jena:jena-arq:4.1.0
