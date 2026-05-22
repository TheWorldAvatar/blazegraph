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
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import com.bigdata.rdf.vocab.VocabularyDecl;

/**
 * Vocabulary and namespace for RDFS.
 * 
 * @see http://www.w3.org/2000/01/rdf-schema#
 * 
 * @author <a href="mailto:thompsonbry@users.sourceforge.net">Bryan Thompson</a>
 * @version $Id$
 */
public class RDFSVocabularyDecl implements VocabularyDecl {

    static private final List<IRI> iris = List.of(
        SimpleValueFactory.getInstance().createIRI(RDFS.NAMESPACE),//
        RDFS.CLASS,//
        RDFS.COMMENT,//
        RDFS.CONTAINER,//
        RDFS.CONTAINERMEMBERSHIPPROPERTY,//
        RDFS.DATATYPE,//
        RDFS.DOMAIN,//
        RDFS.ISDEFINEDBY,//
        RDFS.LABEL,//
        RDFS.LITERAL,//
        RDFS.MEMBER,//
        RDFS.RANGE,//
        RDFS.RESOURCE,//
        RDFS.SEEALSO,//
        RDFS.SUBCLASSOF,//
        RDFS.SUBPROPERTYOF//
    );

    public RDFSVocabularyDecl() {
    }
    
    public Iterator<IRI> values() {

        return iris.iterator();
        
    }

}
