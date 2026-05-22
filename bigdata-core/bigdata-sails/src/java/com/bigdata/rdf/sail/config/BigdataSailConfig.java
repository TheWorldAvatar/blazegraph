
package com.bigdata.rdf.sail.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.util.GraphUtil;
import org.eclipse.rdf4j.model.util.GraphUtilException;
import org.eclipse.rdf4j.sail.config.SailConfigException;
import org.eclipse.rdf4j.sail.config.SailImplConfigBase;

public class BigdataSailConfig extends SailImplConfigBase {

	/*-----------*
	 * Variables *
	 *-----------*/

	private String propertiesFile;

	/*--------------*
	 * Constructors *
	 *--------------*/

	public BigdataSailConfig(final String type) {
		super(type);
	}
	
	/*---------*
	 * Methods *
	 *---------*/

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

    public Properties getProperties() 
            throws FileNotFoundException, IOException {
        
        if (propertiesFile == null) {
            return new Properties();
        }
        
        FileInputStream is = new FileInputStream(new File(propertiesFile));
        Properties props = new Properties();
        props.load(is);
        return props;
        
    }
    
	@Override
	public Resource export(Graph graph)
	{
		Resource implNode = super.export(graph);

		if (propertiesFile != null) {
			graph.add(implNode, BigdataConfigSchema.PROPERTIES, 
                    graph.getValueFactory().createLiteral(propertiesFile));
		}

		return implNode;
	}

	@Override
	public void parse(Graph graph, Resource implNode)
		throws SailConfigException
	{
		super.parse(graph, implNode);

		try {
			Literal propertiesLit = GraphUtil.getOptionalObjectLiteral(
                    graph, implNode, BigdataConfigSchema.PROPERTIES);
			if (propertiesLit != null) {
				setPropertiesFile((propertiesLit).getLabel());
			} else {
                throw new SailConfigException("Properties file required");
            }
		}
		catch (GraphUtilException e) {
			throw new SailConfigException(e.getMessage(), e);
		}
	}
}
