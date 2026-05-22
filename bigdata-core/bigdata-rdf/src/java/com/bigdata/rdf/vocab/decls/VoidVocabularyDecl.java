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
 * Created on Jul 23, 2012
 */

package com.bigdata.rdf.vocab.decls;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import com.bigdata.rdf.vocab.VocabularyDecl;

/**
 * Vocabulary and namespace for VOID.
 * 
 * @see <a href="http://www.w3.org/TR/void/"> Describing Linked Datasets with
 *      the VoiD Vocabulary </a>
 * @see <a href="http://vocab.deri.ie/void/"> Vocabulary of Interlinked Datasets
 *      (VoID) </a>
 * 
 * @author <a href="mailto:thompsonbry@users.sourceforge.net">Bryan Thompson</a>
 * @version $Id: RDFVocabularyDecl.java 4631 2011-06-06 15:06:48Z thompsonbry $
 */
public class VoidVocabularyDecl implements VocabularyDecl {

    private static final SimpleValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    public static final String NAMESPACE = "http://rdfs.org/ns/void#";

    // Classes.
    public static final IRI //
            Dataset = VALUE_FACTORY.createIRI(NAMESPACE, "Dataset"),//
            DatasetDescription = VALUE_FACTORY.createIRI(NAMESPACE, "DatasetDescription"),//
            Linkset = VALUE_FACTORY.createIRI(NAMESPACE, "Linkset"),//
            TechnicalFeature = VALUE_FACTORY.createIRI(NAMESPACE, "TechnicalFeature")//
            ;

    // Properties.
    public static final IRI //
            class_ = VALUE_FACTORY.createIRI(NAMESPACE, "class"),//
            classPartition = VALUE_FACTORY.createIRI(NAMESPACE, "classPartition"),//
            classes = VALUE_FACTORY.createIRI(NAMESPACE, "classes"),//
            dataDump = VALUE_FACTORY.createIRI(NAMESPACE, "dataDump"),//
            distinctObjects = VALUE_FACTORY.createIRI(NAMESPACE, "distinctObjects"),//
            distinctSubjects = VALUE_FACTORY.createIRI(NAMESPACE, "distinctSubjects"),//
            documents = VALUE_FACTORY.createIRI(NAMESPACE, "documents"),//
            entities = VALUE_FACTORY.createIRI(NAMESPACE, "entities"),//
            exampleResource = VALUE_FACTORY.createIRI(NAMESPACE, "exampleResource"),//
            feature = VALUE_FACTORY.createIRI(NAMESPACE, "feature"),//
            inDataset = VALUE_FACTORY.createIRI(NAMESPACE, "inDataset"),//
            linkPredicate = VALUE_FACTORY.createIRI(NAMESPACE, "linkPredicate"),//
            objectsTarget = VALUE_FACTORY.createIRI(NAMESPACE, "objectsTarget"),//
            openSearchDescription = VALUE_FACTORY.createIRI(NAMESPACE, "openSearchDescription"),//
            properties = VALUE_FACTORY.createIRI(NAMESPACE, "properties"),//
            property = VALUE_FACTORY.createIRI(NAMESPACE, "property"),//
            propertyPartition = VALUE_FACTORY.createIRI(NAMESPACE, "propertyPartition"),//
            rootResource = VALUE_FACTORY.createIRI(NAMESPACE, "rootResource"),//
            sparqlEndpoint = VALUE_FACTORY.createIRI(NAMESPACE, "sparqlEndpoint"),//
            subjectsTarget = VALUE_FACTORY.createIRI(NAMESPACE, "subjectsTarget"),//
            subset = VALUE_FACTORY.createIRI(NAMESPACE, "subset"),//
            target = VALUE_FACTORY.createIRI(NAMESPACE, "target"),//
            triples = VALUE_FACTORY.createIRI(NAMESPACE, "triples"),//
            uriLookupEndpoint = VALUE_FACTORY.createIRI(NAMESPACE, "uriLookupEndpoint"),//
            uriRegexPattern = VALUE_FACTORY.createIRI(NAMESPACE, "uriRegexPattern"),//
            uriSpace = VALUE_FACTORY.createIRI(NAMESPACE, "uriSpace"),//
            vocabulary = VALUE_FACTORY.createIRI(NAMESPACE, "vocabulary")//
    ;

    static private final List<IRI> iris = List.of(
            VALUE_FACTORY.createIRI(NAMESPACE),
            // classes
            Dataset,
            DatasetDescription,
            Linkset,
            TechnicalFeature,
            // properties
            class_, classPartition, classes, dataDump, distinctObjects,
            distinctSubjects, documents, entities, exampleResource, feature,
            inDataset, linkPredicate, objectsTarget, openSearchDescription,
            properties, property, propertyPartition, rootResource,
            sparqlEndpoint, subjectsTarget, subset, target, triples,
            uriLookupEndpoint, uriRegexPattern, uriSpace, vocabulary//
    );

    public VoidVocabularyDecl() {
    }
    
    public Iterator<IRI> values() {

        return iris.iterator();
        
    }

}
