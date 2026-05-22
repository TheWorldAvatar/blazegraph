package com.bigdata.rdf.sail;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * 
 * Ticket 967 is an EOF exception on high load of HttpClient requests
 */
public class TestTicket967 extends QuadsTestCase {

	public TestTicket967() {
	}

	public TestTicket967(String arg0) {
		super(arg0);
	}

	public void testBug() throws Exception {
		// try with Sesame MemoryStore:
		executeTest(new SailRepository(new MemoryStore()));

		// try with Bigdata:
		try {
			executeTest(new BigdataSailRepository(getSail()));
		} finally {
			getSail().__tearDownUnitTest();
		}
	}

	private void executeTest(final SailRepository repo)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException, RDFParseException, RDFHandlerException,
			IOException {
		try {
			repo.initialize();
			final RepositoryConnection conn = repo.getConnection();
			try {
				conn.setAutoCommit(false);
				final ValueFactory vf = conn.getValueFactory();
		        final URI uri = vf.createURI("os:/elem/example");
		        // run a query which looks for a statement and then adds it if it is not found.
		        addDuringQueryExec(conn, uri, RDF.TYPE, vf.createURI("os:class/Clazz"));
		        // now try to export the statements.
	        	final RepositoryResult<Statement> stats = conn.getStatements(null, null, null, false);
		        try {
		        	// materialize the newly added statement.
		        	stats.next();
		        } catch (RuntimeException e) {
		        	fail(e.getLocalizedMessage(), e); // With Bigdata this fails
		        } finally {
		        	stats.close();
		        }
		        conn.rollback(); // discard the result (or commit, but do something to avoid a logged warning from Sesame).
			} finally {
				conn.close();
			}
		} finally {
			repo.shutDown();
		}
	}
	
	private void addDuringQueryExec(final RepositoryConnection conn,
			final Resource subj, final URI pred, final Value obj,
			final Resource... ctx) throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		final TupleQuery tq = conn.prepareTupleQuery(QueryLanguage.SPARQL,
        		"select distinct ?s ?p ?o where{?s ?p ?t . ?t <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?o }"
        		);
        tq.setBinding("s", subj);
        tq.setBinding("p", pred);
        tq.setBinding("o", obj);
        final TupleQueryResult tqr = tq.evaluate();
        try {
            if (!tqr.hasNext()) {
                conn.add(subj, pred, obj, ctx);
            }
        } finally {
            tqr.close();
        }
    }
}
