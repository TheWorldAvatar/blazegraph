/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package com.bigdata.rdf.rio.ntriples;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserFactory;

import com.bigdata.rdf.ServiceProviderHook;

/**
 * An RDR-aware {@link RDFParserFactory} for N-Triples parsers.
 * 
 * @author Arjohn Kampman
 * @openrdf
 * 
 * @see http://wiki.blazegraph.com/wiki/index.php/Reification_Done_Right
 */
public class BigdataNTriplesParserFactory implements RDFParserFactory {

	/**
	 * Returns {@link RDFFormat#NTRIPLES_RDR}.
	 */
	@Override
	public RDFFormat getRDFFormat() {
		return ServiceProviderHook.NTRIPLES_RDR;
	}

	/**
	 * Returns a new instance of BigdataNTriplesParser.
	 */
	@Override
	public RDFParser getParser() {
		return new BigdataNTriplesParser();
	}
}
