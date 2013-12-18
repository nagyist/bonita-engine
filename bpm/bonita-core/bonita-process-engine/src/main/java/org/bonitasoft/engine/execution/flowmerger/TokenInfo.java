/**
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.execution.flowmerger;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public class TokenInfo {
    
    public final Long outputTokenRefId;

    public final Long outputParentTokenRefId;
    
    
    public TokenInfo() {
        this.outputTokenRefId = null;
        this.outputParentTokenRefId = null;
    }

    public TokenInfo(Long outputTokenRefId) {
        super();
        this.outputTokenRefId = outputTokenRefId;
        this.outputParentTokenRefId = null;
    }

    public TokenInfo(Long outputTokenRefId, Long outputParentTokenRefId) {
        super();
        this.outputTokenRefId = outputTokenRefId;
        this.outputParentTokenRefId = outputParentTokenRefId;
    }

}