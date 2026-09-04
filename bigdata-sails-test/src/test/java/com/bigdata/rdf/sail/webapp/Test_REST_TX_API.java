/**
Copyright (C) SYSTAP, LLC DBA Blazegraph 2006-2016.  All rights reserved.

Contact:
     SYSTAP, LLC DBA Blazegraph
     2501 Calvert ST NW #106
     Washington, DC 20008
     licenses@blazegraph.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.bigdata.rdf.sail.webapp;

import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;

import com.bigdata.journal.IIndexManager;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.webapp.client.ConnectOptions;
import com.bigdata.rdf.sail.webapp.client.IRemoteTx;
import com.bigdata.rdf.sail.webapp.client.JettyResponseListener;
import com.bigdata.rdf.sail.webapp.client.RemoteTransactionManager;

/**
 * Proxied test suite for testing the transaction management API. The outer
 * class provides a test suite for behaviors that are consistent without regard
 * to whether or not isolatable indices have been enabled. There are then two
 * inner classes that provide tests where we are controlling the configuration
 * and verifying behaviors that are specific to when isolatable indices are /
 * are not enabled.
 * 
 * @param <S>
 * 
 * @see <a href="http://trac.bigdata.com/ticket/1156"> Support read/write
 *      transactions in the REST API</a>
 * 
 *      FIXME Test that a sequence of queries may be isolated by a read-only
 *      transaction and that the queries have snapshot isolation across the
 *      transaction that is preserved even when there are new commit points that
 *      write on the namespace.
 * 
 *      FIXME Write tests in which we force operations where the transaction is
 *      not active and make sure that the API is behaving itself in terms of the
 *      error messages.
 * 
 *      FIXME Write HA tests for transaction coordination. See #1189
 * 
 *      FIXME Modify the multi-tenancy API stress test to obtain a stress test
 *      that also operations against namespaces that are configured with
 *      isolatable index support.
 * 
 *      TODO Test that a transaction may be created without regard to the end
 *      point. The transaction is about the transaction manager, not a given
 *      namespace. Isolation of a namespace is obtained by having the
 *      transaction pin the commit point associated with its start time (its
 *      readsOnCommitTime). Thus we can actually use a single transaction to
 *      coordinate an operation on more than one namespace in the same database.
 * 
 *      TODO Write unit test that is federation specific and which verifies that
 *      read/write transactions are correctly rejected since they are not
 *      supported for scale-out (we do not support distributed 2-phase
 *      transactions).
 * 
 *      FIXME Write a test suite that uses a mixture of unisolated and
 *      read/write transactions. Verify that we can use unisolated transactions
 *      for bulk load and isolated transactions for smaller mutations and that
 *      the indices are consistent (especially, this is concerned with the
 *      revision timestamps on the indices that are used to detect write-write
 *      conflicts in read/write transactions - the unisolated updates need to be
 *      touching those timestamps if the index supports isolation in order for
 *      the read/write transactions to detect a conflict created by an
 *      unisolated update since the read/write transaction was created.)
 */
public class Test_REST_TX_API<S extends IIndexManager> extends
      AbstractTestNanoSparqlClient<S> {

   public Test_REST_TX_API() {

   }

   public Test_REST_TX_API(final String name) {

      super(name);

   }

   public static Test suite() {

      return ProxySuiteHelper.suiteWhenStandalone(
            Test_REST_TX_API.ReadWriteTx.class,
            "test.*", TestMode.quads
      // , TestMode.sids
      // , TestMode.triples
            );

   }

   /**
    * Create an unisolated transaction, verify its metadata, and abort it.
    */
   public void test_CREATE_TX_UNISOLATED_01() throws Exception {

      assertNotNull(m_mgr);
      assertNotNull(m_mgr.getTransactionManager());

      final IRemoteTx tx = m_mgr.getTransactionManager().createTx(
            RemoteTransactionManager.UNISOLATED);

      try {

         assertTrue(tx.isActive());
         assertFalse(tx.isReadOnly());

      } finally {

         tx.abort();

      }

      assertFalse(tx.isActive());
      assertFalse(tx.isReadOnly());

   }

   /**
    * Create an unisolated transaction and commit it. This should be a NOP since
    * nothing is written on the database.
    * 
    * TODO Create an unisolated transaction, write on the transaction, commit
    * the transaction and verify that we can read back the write set after the
    * commit. Note that we can only write on the resulting transaction if the
    * namespace supports isolatable indices.
    */
   public void test_CREATE_TX_UNISOLATED_02() throws Exception {

      assertNotNull(m_mgr);
      assertNotNull(m_mgr.getTransactionManager());

      final IRemoteTx tx = m_mgr.getTransactionManager().createTx(
            RemoteTransactionManager.UNISOLATED);

      try {

      } finally {

         tx.commit();

      }

      assertFalse(tx.isActive());

   }

   /**
    * Create an read-only transaction, verify its metadata, and abort it.
    */
   public void test_CREATE_TX_READ_ONLY_01() throws Exception {

      assertNotNull(m_mgr);
      assertNotNull(m_mgr.getTransactionManager());

      final IRemoteTx tx = m_mgr.getTransactionManager().createTx(
            RemoteTransactionManager.READ_COMMITTED);

      try {

         assertTrue(tx.isActive());
         assertTrue(tx.isReadOnly());

      } finally {

         tx.abort();

      }

      assertFalse(tx.isActive());
      assertTrue(tx.isReadOnly());

   }

   /**
    * Create an read-only transaction and commit it. This should be a NOP since
    * nothing is written on the database.
    * 
    * TODO Actually read on the transaction. Verify that we do not see concurrent
    * updates.
    * 
    * TODO Do something similar with read-historical transactions. Verify that
    * we do not see concurrent updates and that we do not see updates for commit
    * points after the transaction start (this is nearly the same thing, but we
    * also should create the read-only tx only once we know that a commit point
    * has been pinned and that subsequent commits have been applied and verify
    * that the new tx is also reading from the correct commit point.)
    */
   public void test_CREATE_TX_READ_ONLY_02() throws Exception {

      assertNotNull(m_mgr);
      assertNotNull(m_mgr.getTransactionManager());

      final IRemoteTx tx = m_mgr.getTransactionManager().createTx(
            RemoteTransactionManager.READ_COMMITTED);

      try {

      } finally {

         tx.commit();

      }

      assertFalse(tx.isActive());

   }

   /**
    * Verify that an ordinary SPARQL update continues to report the commit
    * performed by its auto-commit connection.
    */
   public void test_nonTransactionalUpdateStillReportsCommit()
         throws Exception {

      final SparqlResponse response = sparqlResponse("update",
            "INSERT DATA { <urn:test:autoCommit> <urn:test:p> <urn:test:o> }");

      assertEquals(HttpServletResponse.SC_OK, response.statusCode);
      assertTrue(response.body.contains("COMMIT:"));
      assertTrue(response.body.matches(
            "(?s).*commitTime=[1-9][0-9]*.*"));
      assertFalse(response.body.contains("commitTime=-1"));
      assertFalse(response.body.contains("UPDATE:"));

   }

   /**
    * Verify that a query is rejected while another REST request owns the same
    * read/write transaction.
    */
   public void test_parallelQueryOnSameTransactionRejected() throws Exception {

      final IRemoteTx tx = newReadWriteTx();
      final BigdataRDFContext context = getRestContext();
      final BigdataRDFContext.TransactionUse use = context
            .tryAcquireTransaction(tx.getTxId());

      assertNotNull(use);
      try {
         assertEquals(HttpServletResponse.SC_CONFLICT,
               sparqlStatus("query", "ASK { ?s ?p ?o }", tx.getTxId()));
      } finally {
         context.releaseTransaction(use);
         tx.abort();
      }

   }

   /**
    * Verify that an update is rejected while another REST request owns the
    * same read/write transaction.
    */
   public void test_parallelUpdateOnSameTransactionRejected() throws Exception {

      final IRemoteTx tx = newReadWriteTx();
      final BigdataRDFContext context = getRestContext();
      final BigdataRDFContext.TransactionUse use = context
            .tryAcquireTransaction(tx.getTxId());

      assertNotNull(use);
      try {
         assertEquals(HttpServletResponse.SC_CONFLICT,
               sparqlStatus("update",
                     "INSERT DATA { <urn:test:s> <urn:test:p> <urn:test:o> }",
                     tx.getTxId()));
      } finally {
         context.releaseTransaction(use);
         tx.abort();
      }

   }

   /**
    * Verify that STATUS, PREPARE, COMMIT, and ABORT are rejected while another
    * REST request owns the transaction.
    */
   public void test_parallelLifecycleOperationsRejected() throws Exception {

      final IRemoteTx tx = newReadWriteTx();
      final BigdataRDFContext context = getRestContext();
      final BigdataRDFContext.TransactionUse use = context
            .tryAcquireTransaction(tx.getTxId());

      assertNotNull(use);
      try {
         for (String action : new String[] { "STATUS", "PREPARE", "COMMIT",
               "ABORT" }) {
            assertEquals(action, HttpServletResponse.SC_CONFLICT,
                  transactionStatus(tx.getTxId(), action));
         }
      } finally {
         context.releaseTransaction(use);
         tx.abort();
      }

   }

   /**
    * Verify that ownership of one transaction does not prevent a request from
    * using a different transaction.
    */
   public void test_differentTransactionsRemainConcurrent() throws Exception {

      final IRemoteTx txA = newReadOnlyTx();
      final IRemoteTx txB = newReadOnlyTx();
      final BigdataRDFContext context = getRestContext();
      final BigdataRDFContext.TransactionUse use = context
            .tryAcquireTransaction(txA.getTxId());

      assertNotNull(use);
      try {
         assertEquals(HttpServletResponse.SC_OK,
               sparqlStatus("query", "ASK { ?s ?p ?o }", txB.getTxId()));
      } finally {
         context.releaseTransaction(use);
         try {
            txB.abort();
         } finally {
            txA.abort();
         }
      }

   }

   /**
    * Verify that a malformed query releases its transaction so a subsequent
    * request can use it.
    */
   public void test_queryFailureReleasesTransaction() throws Exception {

      final IRemoteTx tx = newReadOnlyTx();

      try {
         assertEquals(HttpServletResponse.SC_BAD_REQUEST,
               sparqlStatus("query", "SELECT WHERE {", tx.getTxId()));
         assertEquals(HttpServletResponse.SC_OK,
               sparqlStatus("query", "ASK { ?s ?p ?o }", tx.getTxId()));
      } finally {
         tx.abort();
      }

   }

   /**
    * Verify that a read-only transaction receives the same exclusive REST
    * ownership protection as a read/write transaction.
    */
   public void test_parallelReadOnlyTransactionUseRejected() throws Exception {

      final IRemoteTx tx = newReadOnlyTx();
      final BigdataRDFContext context = getRestContext();
      final BigdataRDFContext.TransactionUse use = context
            .tryAcquireTransaction(tx.getTxId());

      assertNotNull(use);
      try {
         assertEquals(HttpServletResponse.SC_CONFLICT,
               sparqlStatus("query", "ASK { ?s ?p ?o }", tx.getTxId()));
      } finally {
         context.releaseTransaction(use);
         tx.abort();
      }

   }

   /**
    * Verify that a historical commit timestamp is not mistaken for an active
    * read-only transaction identifier and therefore is not locked.
    */
   public void test_historicalTimestampIsNotTransactionLocked()
         throws Exception {

      final IRemoteTx tx = newReadOnlyTx();
      final long timestamp = tx.getReadsOnCommitTime();
      tx.abort();

      final BigdataRDFContext context = getRestContext();
      final BigdataRDFContext.TransactionUse use = context
            .tryAcquireTransaction(timestamp);

      assertNotNull(use);
      try {
         assertEquals(HttpServletResponse.SC_OK,
               sparqlStatus("query", "ASK { ?s ?p ?o }", timestamp));
      } finally {
         context.releaseTransaction(use);
      }

   }

   protected IRemoteTx newReadWriteTx() {

      return m_mgr.getTransactionManager().createTx(
            RemoteTransactionManager.UNISOLATED);

   }

   protected IRemoteTx newReadOnlyTx() {

      return m_mgr.getTransactionManager().createTx(
            RemoteTransactionManager.READ_COMMITTED);

   }

   protected BigdataRDFContext getRestContext() {

      return (BigdataRDFContext) NanoSparqlServer.getWebApp(m_fixture)
            .getServletContext().getAttribute(
                  BigdataServlet.ATTRIBUTE_RDF_CONTEXT);

   }

   protected int sparqlStatus(final String operation, final String sparql,
         final long timestamp) throws Exception {

      return sparqlResponse(operation, sparql, timestamp).statusCode;

   }

   protected SparqlResponse sparqlResponse(final String operation,
         final String sparql) throws Exception {

      final ConnectOptions opts = new ConnectOptions(m_repo
            .getSparqlEndPoint());
      opts.method = "POST";
      opts.addRequestParam(operation, sparql);
      if ("query".equals(operation)) {
         opts.setAcceptHeader(BigdataRDFServlet.MIME_SPARQL_RESULTS_JSON);
      }

      return requestResponse(opts);

   }

   protected SparqlResponse sparqlResponse(final String operation,
         final String sparql, final long timestamp) throws Exception {

      final ConnectOptions opts = new ConnectOptions(m_repo
            .getSparqlEndPoint());
      opts.method = "POST";
      opts.addRequestParam(operation, sparql);
      opts.addRequestParam("timestamp", Long.toString(timestamp));
      if ("query".equals(operation)) {
         opts.setAcceptHeader(BigdataRDFServlet.MIME_SPARQL_RESULTS_JSON);
      }

      return requestResponse(opts);

   }

   protected int transactionStatus(final long txId, final String action)
         throws Exception {

      final ConnectOptions opts = new ConnectOptions(m_serviceURL + "/tx/"
            + txId);
      opts.method = "POST";
      opts.addRequestParam(action);

      return requestStatus(opts);

   }

   protected int requestStatus(final ConnectOptions opts) throws Exception {

      return requestResponse(opts).statusCode;

   }

   protected SparqlResponse requestResponse(final ConnectOptions opts)
         throws Exception {

      JettyResponseListener response = null;
      try {
         response = m_mgr.doConnect(opts);
         final int status = response.getStatus();
         final String body = response.getResponseBody();
         return new SparqlResponse(status, body);
      } finally {
         if (response != null) {
            response.abort();
         }
      }

   }

   protected static class SparqlResponse {

      public final int statusCode;
      public final String body;

      public SparqlResponse(final int statusCode, final String body) {

         this.statusCode = statusCode;
         this.body = body;

      }

   }

   protected void assertAskResult(final boolean expected,
         final String query, final long timestamp) throws Exception {

      final SparqlResponse response = sparqlResponse("query", query,
            timestamp);

      assertEquals(HttpServletResponse.SC_OK, response.statusCode);
      assertTrue(response.body.matches(
            "(?s).*\"boolean\"\\s*:\\s*" + expected + ".*"));

   }

   protected void assertTransactionUnavailable(final long txId,
         final String subject) throws Exception {

      final String ask = "ASK { <" + subject + "> ?p ?o }";
      final String insert = "INSERT DATA { <" + subject
            + "> <urn:test:p> <urn:test:o> }";

      assertEquals(HttpServletResponse.SC_NOT_FOUND,
            sparqlStatus("query", ask, txId));
      assertEquals(HttpServletResponse.SC_NOT_FOUND,
            sparqlStatus("update", insert, txId));

      for (String action : new String[] { "STATUS", "PREPARE", "COMMIT",
            "ABORT" }) {
         assertEquals(action, HttpServletResponse.SC_NOT_FOUND,
               transactionStatus(txId, action));
      }

      assertFalse(m_repo.prepareBooleanQuery(ask).evaluate());

   }

   /**
    * An *extension* of the test suite that uses a namespace that is NOT
    * configured to support read/write transactions. This extension is used to
    * verify that certain operations are NOT permitted when the namespace does
    * not support isolatable indices.
    * <p>
    * Note: This does not change whether or not a transaction may be created,
    * just whether or not the namespace will allow an operation that is isolated
    * by a read/write transaction.
    */
   public static class NoReadWriteTx<S extends IIndexManager> extends
         Test_REST_TX_API<S> {

      @Override
      public Properties getProperties() {

         final Properties p = new Properties(super.getProperties());

         p.setProperty(BigdataSail.Options.ISOLATABLE_INDICES, "false");

         return p;

      }
      
      public NoReadWriteTx() {

      }

      public NoReadWriteTx(final String name) {

         super(name);

      }
   }

   /**
    * An *extension* of the test suite that uses a namespace that is configured
    * to support read/write transactions.
    * <p>
    * Note: This does not change whether or not a transaction may be created,
    * just whether or not the namespace will allow an operation that is isolated
    * by a read/write transaction.
    */
   public static class ReadWriteTx<S extends IIndexManager> extends
         Test_REST_TX_API<S> {

      @Override
      public Properties getProperties() {

         final Properties p = new Properties(super.getProperties());

         p.setProperty(BigdataSail.Options.ISOLATABLE_INDICES, "true");

         return p;

      }

      public ReadWriteTx() {

      }

      public ReadWriteTx(final String name) {

         super(name);

      }

      /**
       * Verify a complete read/write transaction workflow through the REST
       * API, including read-your-own-writes and commit visibility.
       */
      public void test_transactionCommitWorkflow() throws Exception {

         final String a = "urn:test:commitWorkflow:a";
         final String b = "urn:test:commitWorkflow:b";
         final String c = "urn:test:commitWorkflow:c";
         final String d = "urn:test:commitWorkflow:d";
         final String status = "urn:test:status";
         final String derivedFrom = "urn:test:derivedFrom";
         final String anyCommittedData = "ASK { VALUES ?s { <" + a + "> <"
               + b + "> } ?s ?p ?o }";
         final String expectedCommittedData = "ASK { <" + a + "> <"
               + status + "> \"committed\" . <" + b + "> <" + derivedFrom
               + "> <" + a + "> . FILTER NOT EXISTS { <" + c
               + "> ?cp ?co } FILTER NOT EXISTS { <" + d
               + "> ?dp ?do } }";
         final IRemoteTx tx = newReadWriteTx();

         try {
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "INSERT DATA { <" + a + "> <"
                        + status + "> \"pending\" . <" + c + "> <" + status
                        + "> \"delete-data\" . <" + d + "> <" + status
                        + "> \"delete-where\" . }", tx.getTxId()));
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "INSERT { <" + b + "> <"
                        + derivedFrom + "> <" + a + "> } WHERE { <" + a
                        + "> <" + status + "> \"pending\" }", tx.getTxId()));
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "DELETE DATA { <" + c + "> <"
                        + status + "> \"delete-data\" }", tx.getTxId()));
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "DELETE WHERE { <" + d
                        + "> ?p ?o }", tx.getTxId()));
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "DELETE { <" + a + "> <" + status
                        + "> \"pending\" } INSERT { <" + a + "> <" + status
                        + "> \"committed\" } WHERE { <" + a + "> <" + status
                        + "> \"pending\" }", tx.getTxId()));

            assertAskResult(true, expectedCommittedData, tx.getTxId());

            final SparqlResponse selectResponse = sparqlResponse("query",
                  "SELECT ?status WHERE { <" + a + "> <" + status
                        + "> ?status }", tx.getTxId());
            assertEquals(HttpServletResponse.SC_OK,
                  selectResponse.statusCode);
            assertTrue(selectResponse.body.contains("\"committed\""));

            assertFalse(m_repo.prepareBooleanQuery(anyCommittedData)
                  .evaluate());

            tx.commit();
         } finally {
            getRestContext().abortTransactionIfActive(tx.getTxId());
         }

         assertTrue(m_repo.prepareBooleanQuery(expectedCommittedData)
               .evaluate());

      }

      /**
       * Verify that abort discards all writes made by a REST transaction.
       */
      public void test_transactionAbortWorkflow() throws Exception {

         final String a = "urn:test:abortWorkflow:a";
         final String b = "urn:test:abortWorkflow:b";
         final String status = "urn:test:status";
         final String derivedFrom = "urn:test:derivedFrom";
         final String anyTransactionData = "ASK { VALUES ?s { <" + a + "> <"
               + b + "> } ?s ?p ?o }";
         final IRemoteTx tx = newReadWriteTx();

         try {
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "INSERT DATA { <" + a + "> <"
                        + status + "> \"pending\" }", tx.getTxId()));
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "INSERT { <" + b + "> <"
                        + derivedFrom + "> <" + a + "> } WHERE { <" + a
                        + "> <" + status + "> \"pending\" }", tx.getTxId()));

            assertAskResult(true, anyTransactionData, tx.getTxId());
            assertFalse(m_repo.prepareBooleanQuery(anyTransactionData)
                  .evaluate());

            tx.abort();
         } finally {
            getRestContext().abortTransactionIfActive(tx.getTxId());
         }

         assertFalse(m_repo.prepareBooleanQuery(anyTransactionData)
               .evaluate());

      }

      /**
       * Verify that a committed transaction identifier cannot be reused.
       */
      public void test_committedTransactionCannotBeReused() throws Exception {

         final IRemoteTx tx = newReadWriteTx();
         final long txId = tx.getTxId();

         try {
            tx.commit();
            assertTransactionUnavailable(txId,
                  "urn:test:committedTransactionReuse");
         } finally {
            getRestContext().abortTransactionIfActive(txId);
         }

      }

      /**
       * Verify that an aborted transaction identifier cannot be reused.
       */
      public void test_abortedTransactionCannotBeReused() throws Exception {

         final IRemoteTx tx = newReadWriteTx();
         final long txId = tx.getTxId();

         try {
            tx.abort();
            assertTransactionUnavailable(txId,
                  "urn:test:abortedTransactionReuse");
         } finally {
            getRestContext().abortTransactionIfActive(txId);
         }

      }

      /**
       * Verify that an unknown read/write transaction identifier is rejected.
       */
      public void test_unknownTransactionRejected() throws Exception {

         assertTransactionUnavailable(-9223372036854775000L,
               "urn:test:unknownTransaction");

      }

      /**
       * Verify that a successful transactional update reports update
       * completion without claiming that the transaction was committed.
       */
      public void test_transactionalUpdateReportsUpdate() throws Exception {

         final IRemoteTx tx = newReadWriteTx();

         try {
            final SparqlResponse response = sparqlResponse("update",
                  "INSERT DATA { <urn:test:transactionalUpdate> "
                        + "<urn:test:p> <urn:test:o> }", tx.getTxId());

            assertEquals(HttpServletResponse.SC_OK, response.statusCode);
            assertTrue(response.body.contains("UPDATE:"));
            assertTrue(response.body.contains("mutationCount="));
            assertFalse(response.body.contains("COMMIT:"));
            assertFalse(response.body.contains("commitTime="));
         } finally {
            getRestContext().abortTransactionIfActive(tx.getTxId());
         }

      }

      /**
       * Verify that a syntax error aborts a read/write transaction and
       * discards its pending writes.
       */
      public void test_transactionAbortedAfterUpdateSyntaxFailure()
            throws Exception {

         final String subject = "urn:test:syntaxFailure";
         final String predicate = "urn:test:predicate";
         final String object = "urn:test:object";
         final String ask = "ASK { <" + subject + "> <" + predicate + "> <"
               + object + "> }";
         final IRemoteTx tx = newReadWriteTx();

         try {
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "INSERT DATA { <" + subject + "> <"
                        + predicate + "> <" + object + "> }", tx.getTxId()));
            assertEquals(HttpServletResponse.SC_BAD_REQUEST,
                  sparqlStatus("update", "INSERT DATA WHERE { }",
                        tx.getTxId()));
            assertEquals(HttpServletResponse.SC_NOT_FOUND,
                  sparqlStatus("query", ask, tx.getTxId()));
            assertFalse(m_repo.prepareBooleanQuery(ask).evaluate());
         } finally {
            getRestContext().abortTransactionIfActive(tx.getTxId());
         }

      }

      /**
       * Verify that an execution error aborts a read/write transaction and
       * discards its pending writes.
       */
      public void test_transactionAbortedAfterUpdateExecutionFailure()
            throws Exception {

         final String subject = "urn:test:executionFailure";
         final String predicate = "urn:test:predicate";
         final String object = "urn:test:object";
         final String ask = "ASK { <" + subject + "> <" + predicate + "> <"
               + object + "> }";
         final String missingResource = "file:///tmp/blazegraph-missing-"
               + System.nanoTime() + ".ttl";
         final IRemoteTx tx = newReadWriteTx();

         try {
            assertEquals(HttpServletResponse.SC_OK,
                  sparqlStatus("update", "INSERT DATA { <" + subject + "> <"
                        + predicate + "> <" + object + "> }", tx.getTxId()));
            assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                  sparqlStatus("update", "LOAD <" + missingResource + ">",
                        tx.getTxId()));
            assertEquals(HttpServletResponse.SC_NOT_FOUND,
                  sparqlStatus("query", ask, tx.getTxId()));
            assertFalse(m_repo.prepareBooleanQuery(ask).evaluate());
         } finally {
            getRestContext().abortTransactionIfActive(tx.getTxId());
         }

      }
   }

}
