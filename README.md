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

### Enhancement considerations

	Run beams on spark engine?
	Keep track of search/results to detect search patterns
	Auto generate word search pattern (e.g. if pattern of searching two words one after the other is detected, store them as associated words.
	Requires rest endpoint detecting ip address or something similar)

### Command Line Running

#### Search book by word parameters
	1. search command "search_book"
	2. catalog "rdf-files.tar.bz2". currently it only has project gutenberg catalog, but the system is designed in a way to handle more catalogs in the future.
	3. word
	4. result limit (please note result can be less than the limit if that's all it could find)

#### Search word by book parameters
	1. search command "search_word"
	2. catalog "rdf-files.tar.bz2". currently it only has project gutenberg catalog, but the system is designed in a way to handle more catalogs in the future.
	3. book id, title, or author
	4. result limit (please note result can be less than the limit if that's all it could find)
	5. specify search options - (b) for book id, (a) for author, (t) for title. If empty parameter is given, the system will be able to infer from third parameter value.

#### Generate word count by book range parameters
	1. command "count"
	2. catalog "rdf-files.tar.bz2". currently it only has project gutenberg catalog, but the system is designed in a way to handle more catalogs in the future.
	3. range e.g. "305-306". The system will download documents with ID in the range and extract word counts to store in database.
	4. destination folder for documents found
	** If you need to test it, please test it once with 305-306 as Heroku is already warning me about the storage of cloud database with free plan. 

#### Initial setup
	1. command "init"
	2. catalog url "https://gutenberg.org/cache/epub/feeds"
	3. catalog file name "rdf-files.tar.bz2"
	4. destination folder "D:\\Temp"
	5. initial range of books "306-308"
	** THIS IS ALREADY DONE FOR THIS DEMONSTRATION