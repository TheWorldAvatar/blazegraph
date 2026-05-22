/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package com.bigdata.rdf.rio.turtle;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserFactory;
import org.eclipse.rdf4j.rio.turtle.TurtleParser;

import com.bigdata.rdf.ServiceProviderHook;

/**
 * An RDR-aware {@link RDFParserFactory} for Turtle parsers.
 * 
 * @author Arjohn Kampman
 * @openrdf
 * 
 * @see http://wiki.blazegraph.com/wiki/index.php/Reification_Done_Right
 */
public class BigdataTurtleParserFactory implements RDFParserFactory {

	/**
	 * Returns {@link ServiceProviderHook#TURTLE_RDR}.
	 * 
	 * @see <a href="http://trac.blazegraph.com/ticket/1038" >RDR RDF parsers not
	 *      always discovered </a>
	 */
	@Override
	public RDFFormat getRDFFormat() {
		return ServiceProviderHook.TURTLE_RDR;
	}

	/**
	 * Returns a new instance of {@link TurtleParser}.
	 */
	@Override
	public RDFParser getParser() {
		return new BigdataTurtleParser();
	}
}
