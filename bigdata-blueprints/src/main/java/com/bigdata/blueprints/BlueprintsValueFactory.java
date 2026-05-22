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
package com.bigdata.blueprints;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.IRI;

import com.tinkerpop.blueprints.Element;

/**
 * Factory for converting blueprints data to RDF and back again. 
 * 
 * @author mikepersonick
 *
 */
public interface BlueprintsValueFactory {

    /**
     * Return the IRI used for typing elements.
     */
    IRI getTypeURI();
    
    /**
     * Return the IRI used to identify vertices.
     */
    IRI getVertexURI();
    
    /**
     * Return the IRI used to identify edges.
     */
    IRI getEdgeURI();
    
    /**
     * Return the IRI used for labeling edges.
     */
    IRI getLabelURI();
    
    /**
     * Create a vertex IRI from a blueprints vertex id.
     */
	IRI toVertexURI(Object key);

	/**
     * Create an edge IRI from a blueprints edge id.
     */
	IRI toEdgeURI(Object key);
	
    /**
     * Create an element IRI from a blueprints element id.
     */
	IRI toURI(Element e);
	
    /**
     * Create a property IRI from a blueprints property name.
     */
	IRI toPropertyURI(String property);
	
//    /**
//     * Create a blueprints vertex id from a vertex IRI.
//     */
//	String fromVertexURI(IRI IRI);
//
//    /**
//     * Create a blueprints edge id from an edge IRI.
//     */
//	String fromEdgeURI(IRI IRI);
//	
//    /**
//     * Create a blueprints property name from a property IRI.
//     */
//	String fromPropertyURI(IRI IRI);

	String fromURI(IRI IRI);
	
	/**
	 * Create a datatyped literal from a blueprints property value.
	 */
	Literal toLiteral(Object val);
	
	/**
	 * Create a blueprints property value from a datatyped literal.
	 */
	Object fromLiteral(Literal lit);
	
//	/**
//	 * Is the IRI a vertex?
//	 */
//	boolean isVertex(IRI IRI);
//	
//    /**
//     * Is the IRI an edge?
//     */
//	boolean isEdge(IRI IRI);
//	
//    /**
//     * Is the IRI an edge?
//     */
//    boolean isProperty(IRI IRI);
    
}
