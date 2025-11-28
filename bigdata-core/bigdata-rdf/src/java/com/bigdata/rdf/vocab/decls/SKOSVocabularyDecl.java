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
 * Created on Jun 4, 2011
 */

package com.bigdata.rdf.vocab.decls;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.URIImpl;

import com.bigdata.rdf.vocab.VocabularyDecl;

/**
 * Vocabulary and namespace for SKOS.
 * 
 * @see http://www.w3.org/2004/02/skos/core#
 * @see http://www.w3.org/TR/skos-reference/skos.html
 * 
 * @author <a href="mailto:thompsonbry@users.sourceforge.net">Bryan Thompson</a>
 * @version $Id$
 */
public class SKOSVocabularyDecl implements VocabularyDecl {

    private static final ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    public static final String NAMESPACE = "http://www.w3.org/2004/02/skos/core#";
    public static final IRI Collection = VALUE_FACTORY.createIRI(NAMESPACE, "Collection");
    public static final IRI Concept = VALUE_FACTORY.createIRI(NAMESPACE, "Concept");
    public static final IRI ConceptScheme = VALUE_FACTORY.createIRI(NAMESPACE, "ConceptScheme");
    public static final IRI OrderedCollection = VALUE_FACTORY.createIRI(NAMESPACE, "OrderedCollection");
    public static final IRI altLabel = VALUE_FACTORY.createIRI(NAMESPACE, "altLabel");
    public static final IRI broadMatch = VALUE_FACTORY.createIRI(NAMESPACE, "broadMatch");
    public static final IRI broader = VALUE_FACTORY.createIRI(NAMESPACE, "broader");
    public static final IRI broaderTransitive = VALUE_FACTORY.createIRI(NAMESPACE, "broaderTransitive");
    public static final IRI changeNote = VALUE_FACTORY.createIRI(NAMESPACE, "changeNote");
    public static final IRI closeMatch = VALUE_FACTORY.createIRI(NAMESPACE, "closeMatch");
    public static final IRI definition = VALUE_FACTORY.createIRI(NAMESPACE, "definition");
    public static final IRI editorialNote = VALUE_FACTORY.createIRI(NAMESPACE, "editorialNote");
    public static final IRI exactMatch = VALUE_FACTORY.createIRI(NAMESPACE, "exactMatch");
    public static final IRI example = VALUE_FACTORY.createIRI(NAMESPACE, "example");
    public static final IRI hasTopConcept = VALUE_FACTORY.createIRI(NAMESPACE, "hasTopConcept");
    public static final IRI hiddenLabel = VALUE_FACTORY.createIRI(NAMESPACE, "hiddenLabel");
    public static final IRI historyNote = VALUE_FACTORY.createIRI(NAMESPACE, "historyNote");
    public static final IRI inScheme = VALUE_FACTORY.createIRI(NAMESPACE, "inScheme");
    public static final IRI mappingRelation = VALUE_FACTORY.createIRI(NAMESPACE, "mappingRelation");
    public static final IRI member = VALUE_FACTORY.createIRI(NAMESPACE, "member");
    public static final IRI memberList = VALUE_FACTORY.createIRI(NAMESPACE, "memberList");
    public static final IRI narrowMatch = VALUE_FACTORY.createIRI(NAMESPACE, "narrowMatch");
    public static final IRI narrow = VALUE_FACTORY.createIRI(NAMESPACE, "narrow");
    public static final IRI narrowTransitive = VALUE_FACTORY.createIRI(NAMESPACE, "narrowTransitive");
    public static final IRI notation = VALUE_FACTORY.createIRI(NAMESPACE, "notation");
    public static final IRI note = VALUE_FACTORY.createIRI(NAMESPACE, "note");
    public static final IRI prefLabel = VALUE_FACTORY.createIRI(NAMESPACE, "prefLabel");
    public static final IRI related = VALUE_FACTORY.createIRI(NAMESPACE, "related");
    public static final IRI relatedMatch = VALUE_FACTORY.createIRI(NAMESPACE, "relatedMatch");
    public static final IRI scopeNote = VALUE_FACTORY.createIRI(NAMESPACE, "scopeNote");
    public static final IRI semanticRelation= VALUE_FACTORY.createIRI(NAMESPACE, "semanticRelation");
    public static final IRI topConceptOf = VALUE_FACTORY.createIRI(NAMESPACE, "topConceptOf");
    
    static private final List<IRI> iris =List.of(
        VALUE_FACTORY.createIRI(NAMESPACE),//
        Collection, Concept, ConceptScheme, OrderedCollection, altLabel,
        broadMatch, broader, broaderTransitive, changeNote, closeMatch,
        definition, editorialNote, exactMatch, example, hasTopConcept,
        hiddenLabel, historyNote, inScheme, mappingRelation, member,
        memberList, narrowMatch, narrow, narrowTransitive, notation, note,
        prefLabel, related, relatedMatch, scopeNote, semanticRelation,
        topConceptOf//
    );

    public SKOSVocabularyDecl() {
    }
    
    public Iterator<IRI> values() {

        return iris.iterator();
        
    }

}
