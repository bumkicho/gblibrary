package com.bkc.gblibrary.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.bkc.gblibrary.model.BookInfo;
import com.bkc.gblibrary.model.Catalog;
import com.bkc.gblibrary.repository.BookInfoRepository;

/**
 * 
 * @author bumki
 *
 */

@Component
public class CatalogFile {

	public static final String FILE_PREFIX = "pg";
	public static final String FILE_EXTENSION = ".rdf";

	private String filePath;

	private BookInfo book;

	@Autowired
	private BookInfoRepository bookInfoRepository;
	

	public void saveBookInfo(File file, String id, Catalog catalog) throws IOException {
		filePath = file.getCanonicalPath();
		
		book = getBookInfo();

		book.setGbId(id);
		try {
			queryFile(file);
			book.setBookURL("https://www.gutenberg.org/files/"+id+"/"+id+"-0.txt");
			book.setCatalog(catalog);			
			bookInfoRepository.save(book);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	// query a catalog file and save it
	public void saveBookInfo(File folder, Catalog catalog) {
		if (folder == null) {
			throw new IllegalArgumentException("Invalid path to RDF file.");
		}
		String pathToFile = folder.getAbsolutePath() + File.separatorChar + FILE_PREFIX + folder.getName() + FILE_EXTENSION;
		File file = new File(pathToFile);
		if (!file.exists()) {
			throw new IllegalArgumentException("Wrong rdf file. Id: " + folder.getName());
		}
		
		filePath = pathToFile;
		book = getBookInfo();

		book.setGbId(folder.getName());
		try {
			queryFile(file);
			book.setBookURL("https://www.gutenberg.org/files/"+folder.getName()+"/"+folder.getName()+"-0.txt");
			book.setCatalog(catalog);			
			bookInfoRepository.save(book);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Bean
	private BookInfo getBookInfo() {
		return new BookInfo();
	}

	private void queryFile(File file) throws FileNotFoundException {
		InputStream is = new FileInputStream(this.filePath);

		Model model = ModelFactory.createDefaultModel();
		model.read(is, "http://www.gutenberg.org/", "RDF/XML");

		Query query = QueryFactory.create(getQueryStatement());
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		ResultSet results = qexec.execSelect();
		while (results.hasNext()) {
			QuerySolution qsol = results.nextSolution();

			String title = getFieldValue(qsol, "title");
			if (title != null) {
				book.setTitle(title.replaceAll("[\n\r]", ""));
			}
			book.setAuthor(getFieldValue(qsol, "author"));
			book.setLanguage(getFieldValue(qsol, "language"));
		}
	}

	private String getFieldValue(QuerySolution qsol, String field) {
		String value = null;
		Literal literal = qsol.getLiteral(field);
		if (literal != null) {
			value = literal.getString();
		}
		return value;
	}

	private String getQueryStatement() {
		String queryString = "PREFIX dcterms: <http://purl.org/dc/terms/> \n";
		queryString += "PREFIX pgterms: <http://www.gutenberg.org/2009/pgterms/> \n";
		queryString += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n\n";

		queryString += "SELECT ?title ?author (str(?lan) as ?language) ?fileurl \n";
		queryString += " WHERE { ?s dcterms:title ?title . \n";

		queryString += " OPTIONAL { ?u dcterms:creator ?c . \n";
		queryString += " ?c pgterms:name ?author . } \n";

		queryString += " OPTIONAL { ?b dcterms:language ?l . \n";
		queryString += " ?l rdf:value ?lan . }  \n";

		/*
		 * Not sure how to query fileurl and its modified date.
		 */
//		queryString += " OPTIONAL { ?b dcterms:hasFormat ?x . \n";
//		queryString += " ?x pgterms:file ?x . \n";
//		queryString += " ?x rdf:about ?fileurl . }  \n";
		
		queryString += " } \n";

		return queryString;
	}

	public String getFilePath() {
		return filePath;
	}

}