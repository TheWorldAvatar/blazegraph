# Blazegraph Transactions

Blazegraph supports read-only snapshot transactions and fully isolated
read/write transactions. This document distinguishes the transaction support
that already existed in `BigdataSail` from the multi-request REST transaction
support added by this branch.

The REST API continues to use the request parameter name `timestamp` for both
historical timestamps and transaction identifiers. A negative value below
`-1` is a read/write transaction identifier. A positive value can be either a
historical commit timestamp or an active read-only transaction identifier.

## Prerequisites

REST read/write transactions require a standalone `Journal`. Distributed
read/write transactions are not supported by scale-out or federation
deployments.

The target namespace must be created with isolatable indices:

```properties
com.bigdata.rdf.sail.isolatableIndices=true
```

This setting must be applied when the namespace is created. Changing the
property after the indices exist does not make the existing indices
isolatable; create a new namespace and migrate the data instead.

For a triple-mode namespace, transactions cannot be combined with truth
maintenance or inference. A conservative transaction-compatible configuration
includes:

```properties
com.bigdata.rdf.sail.isolatableIndices=true
com.bigdata.rdf.sail.truthMaintenance=false
com.bigdata.rdf.store.AbstractTripleStore.justify=false
com.bigdata.rdf.store.AbstractTripleStore.axiomsClass=com.bigdata.rdf.axioms.NoAxioms
```

Quads mode is also supported. Optional index features should be evaluated
separately if they are expected to participate in the same transactional
workflow; the current REST transaction tests focus on RDF statement updates.

The examples below use these URLs:

```bash
BASE_URL=http://localhost:9999/blazegraph
TX_URL="$BASE_URL/tx"
SPARQL_URL="$BASE_URL/namespace/kb/sparql"
```

The standard WAR maps the transaction servlet at `/blazegraph/tx`. A reverse
proxy may expose it at another path. For example, the TWA Docker routing used by
the transaction shell tests exposes it as `/blazegraph/ui/tx`; set `TX_URL`
accordingly.

## Existing `BigdataSail` Support

The following embedded transaction support existed before this branch.

### Read-only connections

`BigdataSailRepository` provides:

- `getReadOnlyConnection()` for a snapshot at the latest commit point.
- `getReadOnlyConnection(long timestamp)` for a historical committed snapshot.

Read-only connections can run concurrently with writers. Closing the
connection releases the read-only transaction and its read lock.

### Unisolated writable connections

`getUnisolatedConnection()` provides a writable view of the current unisolated
indices. An unisolated update is committed or rolled back by its connection and
is not a fully isolated read/write transaction. Writers against the same Sail
are serialized unless coordinated through the existing group-commit
mechanisms.

### Fully isolated read/write connections

When isolatable indices are enabled, `getReadWriteConnection()` creates a new
fully isolated read/write transaction owned by the connection. Depending on
the Sail configuration, `getConnection()` can select this connection type as
well.

The connection lifecycle is:

- `commit()` or `commit2()` validates and commits the write set, then starts a
  replacement transaction for continued use of the connection.
- `rollback()` aborts the write set, then starts a replacement transaction.
- `close()` aborts the connection's current transaction.

Multiple independent read/write transactions can run concurrently. Blazegraph
can reconcile compatible conflicts, such as add/add conflicts. An
unreconcilable write/write conflict fails during prepare or commit validation.

A typical embedded repository workflow is:

```java
BigdataSailRepositoryConnection connection = null;
boolean committed = false;
try {
    connection = repository.getReadWriteConnection();
    connection.setAutoCommit(false);

    connection.prepareUpdate(QueryLanguage.SPARQL,
            "INSERT DATA { <urn:example:s> <urn:example:p> <urn:example:o> }")
            .execute();

    boolean exists = connection.prepareBooleanQuery(QueryLanguage.SPARQL,
            "ASK { <urn:example:s> <urn:example:p> <urn:example:o> }")
            .evaluate();

    connection.commit();
    committed = true;
} finally {
    if (connection != null) {
        if (!committed) {
            connection.rollback();
        }
        connection.close();
    }
}
```

Queries and updates execute against the connection's transaction view. This
includes repository reads and writes and SPARQL operations supported by the
normal query/update engine. The transaction work in this branch explicitly
covers:

- `SELECT` and `ASK`.
- `INSERT DATA` and `INSERT ... WHERE`.
- `DELETE DATA` and `DELETE ... WHERE`.
- Combined `DELETE ... INSERT ... WHERE`.

Before this branch, a Sail connection could create and own a read/write
transaction, but the repository did not provide a way to attach a new
connection to an existing transaction identifier. The REST transaction
lifecycle endpoints existed, but namespace SPARQL requests did not reliably
reuse the transaction that those endpoints created.

## REST API Support Added by This Branch

This branch allows a transaction to span multiple HTTP requests. It adds an
externally managed Sail connection that attaches to an existing read/write
transaction. Each HTTP request opens and closes its own connection while the
Journal transaction remains active until an explicit commit or abort.

### Transaction types

The REST transaction endpoint can create:

| Request timestamp | Transaction type | Use |
|---:|---|---|
| `0` | Read/write | Query and update an isolated write set |
| `-1` | Read-only | Pin the latest committed snapshot |
| Positive timestamp | Read-only historical | Pin the committed state at or before that timestamp |

The creation response is XML and includes the transaction identifier in its
`txId` attribute.

### Read/write workflow

Create a read/write transaction:

```bash
curl --request POST "$TX_URL?timestamp=0"
```

Save the negative `txId` returned by the response:

```bash
TX_ID=-1788000000000
```

Run a SPARQL Update in that transaction:

```bash
curl --data-urlencode \
  'update=INSERT DATA { <urn:example:s> <urn:example:p> <urn:example:o> }' \
  "$SPARQL_URL?timestamp=$TX_ID"
```

A successful transactional update returns HTTP `200` and an update summary of
the following form:

```text
UPDATE: totalElapsed=..., mutationCount=...
```

`UPDATE` means the request completed and its mutations were flushed into the
transaction. It does not mean that the transaction was committed. Ordinary
updates without a transaction retain the existing `COMMIT` response and a real
commit timestamp.

Query the uncommitted data through the same transaction:

```bash
curl --header 'Accept: application/sparql-results+json' \
  --data-urlencode \
  'query=ASK { <urn:example:s> <urn:example:p> <urn:example:o> }' \
  "$SPARQL_URL?timestamp=$TX_ID"
```

The query sees the transaction's pending writes. The same query without the
`timestamp` parameter does not see them before commit.

Optionally validate the transaction before committing:

```bash
curl --request POST "$TX_URL/$TX_ID?PREPARE"
```

Commit and publish the complete write set:

```bash
curl --request POST "$TX_URL/$TX_ID?COMMIT"
```

Alternatively, discard the complete write set:

```bash
curl --request POST "$TX_URL/$TX_ID?ABORT"
```

Inspect an active transaction with either:

```bash
curl "$TX_URL/$TX_ID?STATUS"
curl --request POST "$TX_URL/$TX_ID?STATUS"
```

After commit or abort, the transaction identifier is no longer active and
subsequent requests using it return HTTP `404`.

### Read-only snapshot workflow

Create a read-only transaction pinned to the latest committed state:

```bash
curl --request POST "$TX_URL?timestamp=-1"
```

Use the returned positive transaction identifier with SPARQL queries:

```bash
curl --header 'Accept: application/sparql-results+json' \
  --data-urlencode 'query=SELECT * WHERE { ?s ?p ?o }' \
  "$SPARQL_URL?timestamp=$TX_ID"
```

The pinned snapshot remains stable across sequential queries even if other
clients commit new data. An update using a read-only transaction or historical
timestamp is rejected with HTTP `409`. Commit or abort the read-only
transaction when finished to release its read lock.

Passing `timestamp=-1` directly to an individual query means read committed; it
does not by itself pin one snapshot across several requests. Create a read-only
transaction when a stable multi-request snapshot is required.

### Supported SPARQL operations

Transaction-scoped REST requests use the normal namespace SPARQL endpoint. The
current integration coverage verifies:

- Queries: `SELECT` and `ASK`.
- Inserts: `INSERT DATA` and `INSERT ... WHERE`.
- Deletes: `DELETE DATA` and `DELETE ... WHERE`.
- Combined mutation: `DELETE ... INSERT ... WHERE`.

Multiple operations may be sent as sequential HTTP requests using the same
transaction identifier. Each new request can read the results of successful
earlier requests in that transaction.

### Isolation and concurrency

- Uncommitted changes are visible to queries using the same read/write
  transaction.
- Uncommitted changes are hidden from ordinary requests and other
  transactions.
- Commit publishes the transaction's complete write set atomically.
- Abort publishes none of its write set.
- Only one REST request may use a given transaction at a time. A parallel
  query, update, status, prepare, commit, or abort request for that transaction
  receives HTTP `409`.
- Requests using different transaction identifiers can execute concurrently.
- The underlying Sail continues to support concurrent independent read/write
  transactions; the REST restriction applies only to simultaneous use of the
  same transaction.

### Failure behavior

A failed SPARQL Update in an active read/write REST transaction aborts the
entire transaction. This applies to parsing, planning, validation, and
execution failures after the request has been associated with that
transaction. There are no REST savepoints, so successful earlier operations in
the same transaction are also discarded. Subsequent requests using the
aborted identifier return HTTP `404`.

A failed query does not abort the transaction because it does not modify the
write set. The transaction-use guard is released and a subsequent request may
continue using the transaction.

Common responses are:

| Situation | HTTP status |
|---|---:|
| Successful transaction creation | `201 Created` |
| Successful query, update, status, prepare, commit, or abort | `200 OK` |
| Malformed transaction/timestamp parameter | `400 Bad Request` |
| Invalid transaction-scoped SPARQL Update syntax | `400 Bad Request`; transaction aborted |
| Unknown, committed, or aborted transaction | `404 Not Found` |
| Read-only or historical transaction used for an update | `409 Conflict` |
| Namespace does not have isolatable indices | `409 Conflict` |
| Parallel use of the same transaction | `409 Conflict` |
| Prepare or commit validation conflict | `409 Conflict` |

## Transaction-specific limitations

- Transaction attachment is supported only for SPARQL query and update
  requests sent to `/namespace/{namespace}/sparql`. The raw RDF-body insert and
  delete endpoints and multipart update endpoints do not attach to a REST
  transaction.
- Read/write REST transactions require a standalone `Journal`. Distributed
  read/write transactions, scale-out/federation transaction attachment, and HA
  failover of active transactions are not supported.
- The high-level remote repository connection API does not provide a complete
  multi-request transaction workflow. Use the REST lifecycle endpoint and pass
  the returned identifier as the SPARQL request's `timestamp` parameter.
- A transaction permits only one active REST request at a time. Parallel CRUD
  or lifecycle requests using the same identifier are rejected rather than
  queued.
- REST savepoints and partial rollback are not supported. A failed
  transaction-scoped update aborts the whole read/write transaction.
- There is no REST transaction expiry or automatic cleanup policy for an
  abandoned transaction. Clients must explicitly commit or abort. Long-lived
  transactions retain transaction state and can delay resource release.
- Cross-namespace REST transaction workflows are not covered by the current
  integration contract, even though transaction identifiers belong to the
  database transaction manager rather than to one namespace.
- The current guaranteed REST operation set is the set listed under
  "Supported SPARQL operations." Other SPARQL query or update forms may use the
  same execution path but are not covered by this branch's transaction tests.
