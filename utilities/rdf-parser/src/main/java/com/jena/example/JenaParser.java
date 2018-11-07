package com.jena.example;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class JenaParser {

    private static final String inputFileName = "file_4.ttl";

    private static void _process() {

        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException("File: " + inputFileName + " not found");
        }

        model.read(inputFileName);

        /*String queryString =

                "Prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "CONSTRUCT {?s ?p ?o} "
                        + "WHERE { "
                        + " ?ss rdfs:label ?s."
                        + " }"
                        + " ?ss rdf:predicate ?p." + " ?ss rdf:object ?o.";*/

        String queryString =

                "Prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "CONSTRUCT {?s ?p ?o} "
                        + "WHERE { "
                        + " ?ss rdf:subject ?s."
                        + " ?ss rdf:predicate ?p." + " ?ss rdf:object ?o. }";

        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            Model results = qexec.execConstruct();
            StmtIterator iter = results.listStatements();
            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
        }

    }

    public static void main(String[] args) {
        JenaParser._process();
    }
}
