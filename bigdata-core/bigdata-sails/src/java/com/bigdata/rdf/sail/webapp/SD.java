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
/*
 * Created on Mar 2, 2012
 */

package com.bigdata.rdf.sail.webapp;

import java.util.Properties;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import com.bigdata.ha.HAGlue;
import com.bigdata.ha.QuorumService;
import com.bigdata.journal.IIndexManager;
import com.bigdata.journal.IJournal;
import com.bigdata.quorum.Quorum;
import com.bigdata.rdf.axioms.Axioms;
import com.bigdata.rdf.axioms.NoAxioms;
import com.bigdata.rdf.axioms.OwlAxioms;
import com.bigdata.rdf.axioms.RdfsAxioms;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.store.AbstractTripleStore;
import com.bigdata.rdf.store.BD;
import com.bigdata.rdf.vocab.decls.VoidVocabularyDecl;
import com.bigdata.service.IBigdataFederation;
import com.bigdata.util.ClassPathUtil;

/**
 * SPARQL 1.1 Service Description vocabulary class.
 * 
 * @see <a href="http://www.w3.org/TR/sparql11-service-description/"> SPARQL 1.1
 *      Service Description </a>
 * 
 * @see https://sourceforge.net/apps/trac/bigdata/ticket/500
 * 
 * @author <a href="mailto:thompsonbry@users.sourceforge.net">Bryan Thompson</a>
 * @version $Id$
 */
public class SD {

    static public final String NS = "http://www.w3.org/ns/sparql-service-description#";
    
    private static final ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    static public final IRI Service = VALUE_FACTORY.createIRI(NS, "Service");

    static public final IRI endpoint = VALUE_FACTORY.createIRI(NS, "endpoint");

    /**
     * <pre>
     * <sd:supportedLanguage rdf:resource="http://www.w3.org/ns/sparql-service-description#SPARQL11Query"/>
     * </pre>
     */
    static public final IRI supportedLanguage = VALUE_FACTORY.createIRI(NS, "supportedLanguage");

    static public final IRI SPARQL10Query = VALUE_FACTORY.createIRI(NS, "SPARQL10Query");

    static public final IRI SPARQL11Query = VALUE_FACTORY.createIRI(NS, "SPARQL11Query");

    static public final IRI SPARQL11Update = VALUE_FACTORY.createIRI(NS, "SPARQL11Update");

    /**
     * Relates an instance of {@link #Service} to a format that is supported for
     * serializing query results. URIs for commonly used serialization formats
     * are defined by Unique URIs for File Formats. For formats that do not have
     * an existing IRI, the <a href="http://www.w3.org/ns/formats/media_type">
     * media_type </a> and <a
     * href="http://www.w3.org/ns/formats/preferred_suffix"> preferred_suffix
     * </a> properties defined in that document SHOULD be used to describe the
     * format.
     * 
     * @see <a href="http://www.w3.org/ns/formats/"> Unique URIs for File
     *      Formats </a>
     */
    //    *<pre>
//   * <sd:resultFormat rdf:resource="http://www.w3.org/ns/formats/RDF_XML"/>
//   * <sd:resultFormat rdf:resource="http://www.w3.org/ns/formats/Turtle"/>
//   * </pre>
    static public final IRI resultFormat = VALUE_FACTORY.createIRI(NS, "resultFormat");

    /**
     * Relates an instance of sd:Service to a format that is supported for
     * parsing RDF input; for example, via a SPARQL 1.1 Update LOAD statement,
     * or when URIs are dereferenced in FROM/FROM NAMED/USING/USING NAMED
     * clauses (see also sd:DereferencesURIs below).
     * <p>
     * URIs for commonly used serialization formats are defined by Unique URIs
     * for File Formats. For formats that do not have an existing IRI, the <a
     * href="http://www.w3.org/ns/formats/media_type"> media_type </a> and <a
     * href="http://www.w3.org/ns/formats/preferred_suffix"> preferred_suffix
     * </a> properties defined in that document SHOULD be used to describe the
     * format.
     * 
     * @see <a href="http://www.w3.org/ns/formats/"> Unique URIs for File
     *      Formats </a>
     */
    static public final IRI inputFormat = VALUE_FACTORY.createIRI(NS, "inputFormat");

    /**
     * <pre>
     * <sd:feature rdf:resource="http://www.w3.org/ns/sparql-service-description#DereferencesURIs"/>
     * </pre>
     */
    static public final IRI feature = VALUE_FACTORY.createIRI(NS, "feature");
    static public final IRI DereferencesURIs = VALUE_FACTORY.createIRI(NS, "DereferencesURIs");
    static public final IRI UnionDefaultGraph = VALUE_FACTORY.createIRI(NS, "UnionDefaultGraph");
    static public final IRI RequiresDataset = VALUE_FACTORY.createIRI(NS, "RequiresDataset");
    static public final IRI EmptyGraphs = VALUE_FACTORY.createIRI(NS, "EmptyGraphs");
    static public final IRI BasicFederatedQuery = VALUE_FACTORY.createIRI(NS, "BasicFederatedQuery");
    
    /**
     * The namespace for the bigdata specific features.
     */
    static public final String BDFNS = BD.NAMESPACE+"/features/";

    /*
     * KB modes.
     */
    static public final IRI ModeTriples = VALUE_FACTORY.createIRI(BDFNS, "KB/Mode/Triples");
    static public final IRI ModeQuads = VALUE_FACTORY.createIRI(BDFNS, "KB/Mode/Quads");
    static public final IRI ModeSids = VALUE_FACTORY.createIRI(BDFNS, "KB/Mode/Sids");

    /*
     * Text Index modes.
     */
    static public final IRI TextIndexValueCentric = VALUE_FACTORY.createIRI(BDFNS, "KB/TextIndex/ValueCentric");
    
    static public final IRI TextIndexSubjectCentric = VALUE_FACTORY.createIRI(BDFNS, "KB/TextIndex/SubjectCentric");

    /*
     * Misc KB features.
     */
    static public final IRI TruthMaintenance = VALUE_FACTORY.createIRI(BDFNS, "KB/TruthMaintenance");
    
    static public final IRI IsolatableIndices = VALUE_FACTORY.createIRI(BDFNS, "KB/IsolatableIndices");

    /**
     * A highly available deployment - this feature refers to the presence of
     * the {@link HAGlue} interface, the capability for online backups, and the
     * existence of a targer {@link #ReplicationFactor}. You must consult the
     * target {@link #ReplicationFactor} in order to determine whether the
     * database is in principle capable of tolerating one or more failures and
     * the actual #of running joined instances to determine whether the database
     * can withstand a failure.
     */
    static public final IRI HighlyAvailable = VALUE_FACTORY.createIRI(BDFNS, "HighlyAvailable");

    /**
     * The value of this feature is the target replication factor for the
     * database expressed as an <code>xsd:int</code>. If this is ONE (1), then
     * the database is setup with a quorum and has the capability for online
     * backup, but it is not replicated. TWO (2) indicates mirroring, but is not
     * highly available. THREE (3) is the minimum configuration that can
     * withstand a failure.
     */
    static public final IRI ReplicationFactor = VALUE_FACTORY.createIRI(BDFNS, "replicationFactor");
    
    /**
     * An {@link IBigdataFederation}.
     */
    static public final IRI ScaleOut = VALUE_FACTORY.createIRI(BDFNS, "ScaleOut");

    /**
     * This indicates that the namespace is compatible with mapgraph (aka GPU)
     * acceleration, but this does not indicate whether or not the namespace is
     * currently loaded in the mapgraph-runtime.
     */
    static public final IRI MapgraphCompatible = VALUE_FACTORY.createIRI(BDFNS, "MapgraphCompatible");

    /**
     * This indicates that the namespace is currently loaded within the
     * mapgraph-runtime.
     */
    static public final IRI MapgraphAcceleration = VALUE_FACTORY.createIRI(BDFNS, "MapgraphAcceleration");

    /**
     * The <code>namespace</code> for this KB instance as configured by the
     * {@link BigdataSail.Options#NAMESPACE} property.
     */
    static public final IRI KB_NAMESPACE = VALUE_FACTORY.createIRI(BDFNS, "KB/Namespace");
    
    /**
     * <pre>
     * http://www.w3.org/ns/entailment/OWL-RDF-Based
     * http://www.w3.org/ns/entailment/RDFS
     * http://www.w3.org/ns/owl-profile/RL
     * </pre>
     * 
     * @see <a href="http://www.w3.org/ns/entailment/"> Unique URIs for Semantic
     *      Web Entailment Regimes [ENTAILMENT] (members of the class
     *      sd:EntailmentRegime usable with the properties
     *      sd:defaultEntailmentRegime and sd:entailmentRegime) </a>
     *      
     * @see <a href="http://www.w3.org/ns/owl-profile/"> Unique URIs for OWL 2
     *      Profiles [OWL2PROF] (members of the class sd:EntailmentProfile
     *      usable with the properties sd:defaultSupportedEntailmentProfile and
     *      sd:supportedEntailmentProfile) </a>
     */
    static public final IRI defaultEntailmentRegime = VALUE_FACTORY.createIRI(NS, "defaultEntailmentRegime");

    static public final IRI entailmentRegime = VALUE_FACTORY.createIRI(NS, "entailmentRegime");

    static public final IRI supportedEntailmentProfile = VALUE_FACTORY.createIRI(NS, "supportedEntailmentProfile");

    static public final IRI extensionFunction = VALUE_FACTORY.createIRI(NS, "extensionFunction");

    /*
     * Entailment regimes.
     */
    
    /**
     * Simple Entailment
     * 
     * @see http://www.w3.org/ns/entailment/Simple
     */
    static public final IRI simpleEntailment = VALUE_FACTORY.createIRI("http://www.w3.org/ns/entailment/Simple");

    /**
     * RDF Entailment
     * 
     * @see http://www.w3.org/ns/entailment/RDF
     */
    static public final IRI rdfEntailment = VALUE_FACTORY.createIRI("http://www.w3.org/ns/entailment/RDF");

    /**
     * RDFS Entailment
     * 
     * @see http://www.w3.org/ns/entailment/RDFS
     */
    static public final IRI rdfsEntailment = VALUE_FACTORY.createIRI("http://www.w3.org/ns/entailment/RDFS");
    
    static public final IRI Function = VALUE_FACTORY.createIRI(NS, "Function");
    static public final IRI defaultDataset = VALUE_FACTORY.createIRI(NS, "defaultDataset");
    static public final IRI Dataset = VALUE_FACTORY.createIRI(NS, "Dataset");
    static public final IRI defaultGraph = VALUE_FACTORY.createIRI(NS, "defaultGraph");
    static public final IRI Graph = VALUE_FACTORY.createIRI(NS, "Graph");
    static public final IRI namedGraph = VALUE_FACTORY.createIRI(NS, "namedGraph");
    static public final IRI NamedGraph = VALUE_FACTORY.createIRI(NS, "NamedGraph");
    static public final IRI name = VALUE_FACTORY.createIRI(NS, "name");

    /*
     * RDF data
     * 
     * TODO RDFa: http://www.w3.org/ns/formats/RDFa
     */
    
    /**
     * Unique IRI for RDF/XML
     * 
     * @see http://www.w3.org/ns/formats/
     */
    static public final IRI RDFXML = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/RDF_XML");
    
    /**
     * Unique IRI for NTRIPLES
     * 
     * @see http://www.w3.org/ns/formats/
     */
    static public final IRI NTRIPLES = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/N-Triples");

    /**
     * Unique IRI for TURTLE
     * 
     * @see http://www.w3.org/ns/formats/
     */
    static public final IRI TURTLE = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/Turtle");

    // RDR specific extension of TURTLE.
    static public final IRI TURTLE_RDR = VALUE_FACTORY.createIRI("http://www.bigdata.com/ns/formats/Turtle-RDR");

    /**
     * Unique IRI for N3.
     * 
     * @see http://www.w3.org/ns/formats/
     */
    static public final IRI N3 = new URIImpl("http://www.w3.org/ns/formats/N3");

    // /**
    // * TODO The TriX file format.
    // */
    // public static final RDFFormat TRIX = new RDFFormat("TriX",
    // "application/trix", Charset.forName("UTF-8"),
    // Arrays.asList("xml", "trix"), false, true);

    /**
     * The <a
     * href="http://www.wiwiss.fu-berlin.de/suhl/bizer/TriG/Spec/">TriG</a> file
     * format.
     */
    static public final IRI TRIG = VALUE_FACTORY.createIRI("http://www.wiwiss.fu-berlin.de/suhl/bizer/TriG/Spec/");

    // /**
    // * TODO A binary RDF format (openrdf)
    // *
    // * @see http://www.openrdf.org/issues/browse/RIO-79 (Request for unique
    // IRI)
    // */
    // public static final RDFFormat BINARY = new RDFFormat("BinaryRDF",
    // "application/x-binary-rdf", null,
    // "brf", true, true);

    /**
     * The IRI that identifies the N-Quads syntax is
     * <code>http://sw.deri.org/2008/07/n-quads/#n-quads</code>.
     * 
     * @see http://sw.deri.org/2008/07/n-quads/
     */
    static public final IRI NQUADS = VALUE_FACTORY.createIRI("http://sw.deri.org/2008/07/n-quads/#n-quads");

    // RDR specific extension of N-Triples.
    static public final IRI NTRIPLES_RDR = VALUE_FACTORY.createIRI("http://www.bigdata.com/ns/formats/N-Triples-RDR");

    /*
     * SPARQL results
     */

    /**
     * Unique IRI for SPARQL Results in XML
     * 
     * @see http://www.w3.org/ns/formats/
     */
    static public final IRI SPARQL_RESULTS_XML = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/SPARQL_Results_XML");

    /**
     * Unique IRI for SPARQL Results in JSON
     * 
     * @see http://www.w3.org/ns/formats/
     * 
     *      TODO Does openrdf support this yet?
     */
    static public final IRI SPARQL_RESULTS_JSON = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/SPARQL_Results_JSON");

    /**
     * Unique IRI for SPARQL Results in CSV
     * 
     * @see http://www.w3.org/ns/formats/
     */
    static public final IRI SPARQL_RESULTS_CSV = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/SPARQL_Results_CSV");

    /**
     * Unique IRI for SPARQL Results in TSV
     * 
     * @see http://www.w3.org/ns/formats/
     */
    static public final IRI SPARQL_RESULTS_TSV = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/SPARQL_Results_TSV");

    // TODO openrdf binary format.
//    /**
//     * Unique IRI for SPARQL Results in TSV
//     * 
//     * @see TupleQueryResultFormat#BINARY
//     * 
//     * @see http://www.openrdf.org/issues/browse/RIO-79 (Request for unique IRI) 
//     */
//    static public final IRI SPARQL_RESULTS_OPENRDF_BINARY = VALUE_FACTORY.createIRI("http://www.w3.org/ns/formats/SPARQL_Results_TSV");

    /**
     * The graph in which the service description is accumulated (from the
     * constructor).
     */
    protected final Model g;
    
    /**
     * The KB instance that is being described (from the constructor).
     */
    protected final AbstractTripleStore tripleStore;
    
    /**
     * The service end point (from the constructor).
     */
    protected final String[] serviceURI;

    /**
     * The value factory used to create values for the service description graph
     * {@link #g}.
     */
    protected final ValueFactory f;
    
    /**
     * The resource which models the service.
     */
    protected final BNode aService;

    /**
     * The resource which models the default data set for the service.
     */
    protected final BNode aDefaultDataset;
    
//    /**
//     * The resource which models the default graph in the default data set for
//     * the service.
//     */
//    protected final BNode aDefaultGraph;

    /**
     * 
     * @param g
     *            Where to assemble the description.
     * @param tripleStore
     *            The KB instance to be described.
     * @param serviceURIs
     *            One or more service end points for that KB instance.
     * 
     * @see #describeService()
     */
    public SD(final Model g, final AbstractTripleStore tripleStore,
            final String... serviceURI) {

        if (g == null)
            throw new IllegalArgumentException();

        if (tripleStore == null)
            throw new IllegalArgumentException();
        
        if (serviceURI == null)
            throw new IllegalArgumentException();

        if (serviceURI.length == 0)
            throw new IllegalArgumentException();

        for (String s : serviceURI)
            if (s == null)
                throw new IllegalArgumentException();

        this.g = g;
        
        this.tripleStore = tripleStore;
        
        this.serviceURI = serviceURI;
        
        this.f = VALUE_FACTORY;
        
        aService = f.createBNode("service");

        aDefaultDataset = f.createBNode("defaultDataset");
        
    }
    
    /**
     * Collect various information, building up a service description graph.
     * 
     * @param describeStatistics
     *            When <code>true</code>, the VoID description will include the
     *            {@link VoidVocabularyDecl#vocabulary} declarations, the
     *            property partition statistics, and the class partition
     *            statistics.
     * @param describeNamedGraphs
     *            When <code>true</code> and the KB instance is in the
     *            <code>quads</code> mode, each named graph will also be
     *            described in in the same level of detail as the default graph.
     *            Otherwise only the default graph will be described.
     */
    public void describeService(final boolean describeStatistics,
            final boolean describeNamedGraphs) {

        g.add(aService, RDF.TYPE, SD.Service);

        // Service end point.
        describeServiceEndpoints();

        // Describe the supported languages (SPARQL Query, Update, etc).
        describeLanguages();

        // Describe other supported features.
        describeOtherFeatures();
        
        // Describe the supported data formats.
        describeInputFormats();
        describeResultFormats();

        // Describe the entailment regime (if any).
        describeEntailmentRegime();
        
        // Describe the data set for this KB instance.
        {
         
            // Default data set
            g.add(aService, SD.defaultDataset, aDefaultDataset);

            g.add(aDefaultDataset, RDF.TYPE, SD.Dataset);

            final VoID v = new VoID(g, tripleStore, serviceURI, aDefaultDataset);

            v.describeDataSet(describeStatistics, describeNamedGraphs);

        }

    }

    /**
     * Describe the service end point(s).
     * 
     * @see #endpoint
     */
    protected void describeServiceEndpoints() {

        for (String iri : serviceURI) {

            g.add(aService, SD.endpoint, VALUE_FACTORY.createIRI(iri));

        }

    }
    
    /**
     * Describe the supported Query Languages
     */
    protected void describeLanguages() {

        g.add(aService, SD.supportedLanguage, SD.SPARQL10Query);
        
        g.add(aService, SD.supportedLanguage, SD.SPARQL11Query);

        g.add(aService, SD.supportedLanguage, SD.SPARQL11Update);

    }

    /**
     * Describe the supported input formats.
     * 
     * @see http://www.openrdf.org/issues/browse/RIO-79 (Request for unique
     *      URIs)
     * 
     * @see #inputFormat
     */
    protected void describeInputFormats() {
        
        g.add(aService, SD.inputFormat, SD.RDFXML);
        g.add(aService, SD.inputFormat, SD.NTRIPLES);
        g.add(aService, SD.inputFormat, SD.TURTLE);
        g.add(aService, SD.inputFormat, SD.N3);
        // g.add(service, SD.inputFormat, SD.TRIX); // TODO TRIX
        g.add(aService, SD.inputFormat, SD.TRIG);
        // g.add(service, SD.inputFormat, SD.BINARY); // TODO BINARY
        g.add(aService, SD.inputFormat, SD.NQUADS);
		if (tripleStore.getStatementIdentifiers()) {
			// RDR specific data interchange.
			g.add(aService, SD.inputFormat, SD.NTRIPLES_RDR);
			g.add(aService, SD.inputFormat, SD.TURTLE_RDR);
        }
        g.add(aService, SD.inputFormat, SD.SPARQL_RESULTS_XML);
        g.add(aService, SD.inputFormat, SD.SPARQL_RESULTS_JSON);
        g.add(aService, SD.inputFormat, SD.SPARQL_RESULTS_CSV);
        g.add(aService, SD.inputFormat, SD.SPARQL_RESULTS_TSV);
        // g.add(service,SD.inputFormat,SD.SPARQL_RESULTS_OPENRDF_BINARY);

    }

    /**
     * Describe the supported result formats.
     * 
     * @see #resultFormat
     */
    protected void describeResultFormats() {

        g.add(aService, SD.resultFormat, SD.RDFXML);
        g.add(aService, SD.resultFormat, SD.NTRIPLES);
        g.add(aService, SD.resultFormat, SD.TURTLE);
        g.add(aService, SD.resultFormat, SD.N3);
        // g.add(service, SD.resultFormat, SD.TRIX); // TODO TRIX
        g.add(aService, SD.resultFormat, SD.TRIG);
        // g.add(service, SD.resultFormat, SD.BINARY); // TODO BINARY
        // g.add(service, SD.resultFormat, SD.NQUADS); // TODO NQuads
        // writer

        g.add(aService, SD.resultFormat, SD.SPARQL_RESULTS_XML);
        g.add(aService, SD.resultFormat, SD.SPARQL_RESULTS_JSON);
        g.add(aService, SD.resultFormat, SD.SPARQL_RESULTS_CSV);
        g.add(aService, SD.resultFormat, SD.SPARQL_RESULTS_TSV);
        // g.add(service, SD.resultFormat,
        // SD.SPARQL_RESULTS_OPENRDF_BINARY);

    }
    
    /**
     * Describe non-language features.
     * <p>
     * Note: Do NOT report the backing store type (Journal, TemporaryStore,
     * Federation), the journal buffer mode, etc. here. That is all back end
     * stuff and should be visible through an API which can be separately
     * secured. The ServiceDescription is supposed to be "web" facing so it
     * should not have that low level stuff.
     * 
     * TODO sd:languageExtension or sd:feature could be used for query hints,
     * NAMED SUBQUERY, the NSS REST API features, etc.
     */
    protected void describeOtherFeatures() {

        g.add(aService, SD.feature, SD.BasicFederatedQuery);

        if (tripleStore.isQuads()) {

            g.add(aService, SD.feature, SD.UnionDefaultGraph);

            g.add(aService, SD.feature, ModeQuads);

        } else if(tripleStore.isStatementIdentifiers()) {
        
            g.add(aService, SD.feature, ModeSids);
            
        } else {

            g.add(aService, SD.feature, ModeTriples);

        }

        if (tripleStore.getLexiconRelation().isTextIndex()) {

            g.add(aService, SD.feature, TextIndexValueCentric);

        }

        if (tripleStore.getLexiconRelation().isSubjectCentricTextIndex()) {
			/*
			 * @deprecated Feature was never completed due to scalability
			 * issues. See BZLG-1548, BLZG-563.  Code should be removed.
			 */
            g.add(aService, SD.feature, TextIndexSubjectCentric);

        }

        final Properties properties = tripleStore.getProperties();

        if (Boolean.valueOf(properties.getProperty(
                BigdataSail.Options.TRUTH_MAINTENANCE,
                BigdataSail.Options.DEFAULT_TRUTH_MAINTENANCE))) {

            g.add(aService, SD.feature, TruthMaintenance);

        }
        
        if (Boolean.valueOf(properties.getProperty(
                BigdataSail.Options.ISOLATABLE_INDICES,
                BigdataSail.Options.DEFAULT_ISOLATABLE_INDICES))) {

            g.add(aService, SD.feature, IsolatableIndices);

        }

        if (isMapgraphCompatible()) {

            g.add(aService, SD.feature, MapgraphCompatible);

            if (isMapgraphAccelerated()) {

                g.add(aService, SD.feature, MapgraphAcceleration);

            }

        }

        {

            final IIndexManager indexManager = tripleStore.getIndexManager();

            if (indexManager instanceof IJournal) {

                final IJournal jnl = (IJournal) indexManager;

                final Quorum<HAGlue, QuorumService<HAGlue>> quorum = jnl
                        .getQuorum();
                
                if (quorum != null) {

                    final int k = quorum.replicationFactor();
                    
                    g.add(aService, SD.ReplicationFactor, tripleStore
                            .getValueFactory().createLiteral(k));

                    g.add(aService, SD.feature, HighlyAvailable);

                }

            } else if (indexManager instanceof IBigdataFederation) {

                g.add(aService, SD.feature, ScaleOut);

            }

        }

    }
    
    /**
     * Describe the entailment regime.
     * 
     * TODO The Axioms interface could self-report this.
     */
    protected void describeEntailmentRegime() {

        if (!tripleStore.isQuads())
            return;

        final IRI entailmentRegime;
        final Axioms axioms = tripleStore.getAxioms();
        if (axioms == null || axioms instanceof NoAxioms) {
            entailmentRegime = SD.simpleEntailment;
        } else if (axioms instanceof OwlAxioms) {
            // TODO This is really the RDFS+ entailment regime.
            entailmentRegime = SD.rdfsEntailment;
        } else if (axioms instanceof RdfsAxioms) {
            entailmentRegime = SD.rdfsEntailment;
        } else {
            // Unknown.
            entailmentRegime = null;
        }
        if (entailmentRegime != null)
            g.add(aService, SD.entailmentRegime, entailmentRegime);

    }

    /**
     * Return true iff the namespace is compatible with mapgraph acceleration.
     * 
     * @see https://github.com/SYSTAP/bigdata-gpu/issues/134 (Need means to
     *      publish/update/drop graph in the mapgraph-runtime)
     */
    boolean isMapgraphCompatible() {

        final IFeatureSupported obj = ClassPathUtil.classForName(//
                "com.blazegraph.rdf.gpu.MapgraphCompatibleNamespace", // preferredClassName,
                null, // defaultClass,
                IFeatureSupported.class, // sharedInterface,
                getClass().getClassLoader() // classLoader
        );

        if (obj == null)
            return false;

        return obj.isSupported(tripleStore);

    }
    
    /**
     * Return true iff the namespace is currently loaded in the mapgraph runtime.
     * 
     * @see https://github.com/SYSTAP/bigdata-gpu/issues/134 (Need means to
     *      publish/update/drop graph in the mapgraph-runtime)
     */
    boolean isMapgraphAccelerated() {

        final IFeatureSupported obj = ClassPathUtil.classForName(//
                "com.blazegraph.rdf.gpu.MapgraphAcceleratedNamespace", // preferredClassName,
                null, // defaultClass,
                IFeatureSupported.class, // sharedInterface,
                getClass().getClassLoader() // classLoader
        );

        if (obj == null)
            return false;

        return obj.isSupported(tripleStore);

    }
    
}
