/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.springframework.autobuilds.ejbtest.simple;


/**
 * <p>Business interface for a simple EJB test.</p>
 * 
 * @author colin sampaleanu
 * @version $Id: SimpleService.java,v 1.2 2004-07-15 00:07:56 colins Exp $
 */
public interface SimpleService {

	public String echo(String input);
	
	public String echo2(String input);

}
