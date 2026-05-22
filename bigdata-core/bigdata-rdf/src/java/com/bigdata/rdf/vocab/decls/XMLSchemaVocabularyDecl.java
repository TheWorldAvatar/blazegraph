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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.model.vocabulary.XSD;

import com.bigdata.rdf.vocab.VocabularyDecl;

/**
 * Vocabulary and namespace for {@link XSD}.
 * 
 * @see http://www.w3.org/2001/XSD#
 * 
 * @author <a href="mailto:thompsonbry@users.sourceforge.net">Bryan Thompson</a>
 * @version $Id$
 */
public class XMLSchemaVocabularyDecl implements VocabularyDecl {

    static private final IRI[] uris = new IRI[]{
        SimpleValueFactory.getInstance().createIRI(XSD.NAMESPACE), //
        XSD.ANYURI, //
        XSD.BASE64BINARY, //
        XSD.BOOLEAN, //
        XSD.BYTE, //
        XSD.DATE, //
        XSD.DATETIME, //
        XSD.DECIMAL, //
        XSD.DOUBLE, //
        XSD.DURATION, //
        XSD.ENTITIES, //
        XSD.ENTITY, //
        XSD.FLOAT, //
        XSD.GDAY, //
        XSD.GMONTH, //
        XSD.GMONTHDAY, //
        XSD.GYEAR, //
        XSD.GYEARMONTH, //
        XSD.HEXBINARY, //
        XSD.ID, //
        XSD.IDREF, //
        XSD.IDREFS, //
        XSD.INT, //
        XSD.INTEGER, //
        XSD.LANGUAGE, //
        XSD.LONG, //
        XSD.NAME, //
        XSD.NCNAME, //
        XSD.NEGATIVE_INTEGER, //
        XSD.NMTOKEN, //
        XSD.NMTOKENS, //
        XSD.NON_NEGATIVE_INTEGER, //
        XSD.NON_POSITIVE_INTEGER, //
        XSD.NORMALIZEDSTRING, //
        XSD.NOTATION, //
        XSD.POSITIVE_INTEGER, //
        XSD.QNAME, //
        XSD.SHORT, //
        XSD.STRING, //
        XSD.TIME, //
        XSD.TOKEN, //
        XSD.UNSIGNED_BYTE, //
        XSD.UNSIGNED_INT, //
        XSD.UNSIGNED_LONG, //
        XSD.UNSIGNED_SHORT, //
    };

    public XMLSchemaVocabularyDecl() {
    }
    
    public Iterator<IRI> values() {

        return Collections.unmodifiableList(Arrays.asList(uris)).iterator();
        
    }

}
