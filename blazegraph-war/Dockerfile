FROM tomcat:9.0

# Install zip in order to modify .war file
RUN apt-get update && apt-get install zip -y && rm -rf /var/lib/apt/lists/*

# Overwrite tomcat-users.xml to add blazegraph role
COPY docker/tomcat/conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml

# Configuration files can't just be copied in, as they'd get overwritten when the war file is
# deployed. Instead, copy them to a temporary location and use an entrypoint script to add them to
# the war file before tomcat is started.
COPY docker/blazegraph_war /tmp/blazegraph

# Copy in Blazegraph .war file
COPY target/blazegraph-war-*.war /usr/local/tomcat/webapps/blazegraph.war

# Copy in script and set it as the entry point
COPY docker/docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]