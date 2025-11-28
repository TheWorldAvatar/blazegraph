package com.bigdata.gom;

import java.io.IOException;
import java.net.URL;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;

import com.bigdata.gom.om.IObjectManager;

public interface IGOMProxy {
	
	IObjectManager getObjectManager();
	
	ValueFactory getValueFactory();

	void proxySetup() throws Exception;

	void proxyTearDown() throws Exception;

	void load(URL n3, RDFFormat n32) throws IOException, RDFParseException, RepositoryException;

}
