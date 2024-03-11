package com.datastax.astra.devops.org.exception;

/*-
 * #%L
 * Astra Cli
 * %%
 * Copyright (C) 2022 DataStax
 * %%
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Role not found.
 */
public class RoleNotFoundException extends RuntimeException {

    /** Serial Number. */
    private static final long serialVersionUID = -1269813351970244235L;
   
    /**
     * Constructor with roleName
     * 
     * @param roleName
     *      role name
     */
    public RoleNotFoundException(String roleName) {
        super("Role '" + roleName + "' has not been found.");
    }

}
