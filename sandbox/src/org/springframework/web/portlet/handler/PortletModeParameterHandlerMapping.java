/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 */ 

package org.springframework.web.portlet.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;

/**
 * <p>Implementation of the HandlerMapping interface to map from
 * the current PortletMode and a request parameter to request handler beans.
 * The mapping consists of two levels: first the PortletMode and then the 
 * parameter value.  In order to be mapped, both elements must
 * match the mapping definition.</p>
 * 
 * <p>This is a combination of the methods used in {@link PortletModeHandlerMapping PortletModeHandlerMapping}
 * and {@link ParameterHandlerMapping ParameterHandlerMapping}.  Unlike
 * those two classes, this mapping cannot be initialized with properties since it
 * requires a two-level map.</p>
 * 
 * <p>The default name of the parameter is "action", but can be changed using
 * {@link #setParameterName setParameterName()}.</p>
 *
 * <p>By default, the same parameter value may not be used in two different portlet
 * modes.  This is so that if the portal itself changes the portlet mode, the request 
 * will no longer be valid in the mapping.  This behavior can be changed with 
 * {@link #setAllowDupParameters setAllowDupParameters()}.</p>
 * 
 * <p>The bean configuration for this mapping will look somthing like this:</p>
 * <pre>
 * 	&lt;bean id="portletModeParameterHandlerMapping" class="org.springframework.web.portlet.handler.PortletModeParameterHandlerMapping"&gt;
 *		&lt;property name="portletModeParameterMap"&gt;
 *			&lt;map&gt;
 *				&lt;entry key="view"&gt; &lt;!-- portlet mode: view --&gt;
 *					&lt;map&gt;
 *						&lt;entry key="add"&gt;&lt;ref bean="addItemHandler"/&gt;&lt;/entry&gt;
 *						&lt;entry key="edit"&gt;&lt;ref bean="editItemHandler"/&gt;&lt;/entry&gt;
 *						&lt;entry key="delete"&gt;&lt;ref bean="deleteItemHandler"/&gt;&lt;/entry&gt;
 *					&lt;/map&gt;
 *				&lt;/entry&gt;
 *				&lt;entry key="edit"&gt; &lt;!-- portlet mode: edit --&gt;
 *					&lt;map&gt;
 *						&lt;entry key="prefs"&gt;&lt;ref bean="preferencesHandler"/&gt;&lt;/entry&gt;
 *						&lt;entry key="resetPrefs"&gt;&lt;ref bean="resetPreferencesHandler"/&gt;&lt;/entry&gt;
 *					&lt;/map&gt;
 *				&lt;/entry&gt;
 *			&lt;/map&gt;
 *		&lt;/property&gt;
 *	&lt;/bean&gt;
 * </pre>
 *
 * <p>This mapping can be chained ahead of a {@link PortletModeHandlerMapping PortletModeHandlerMapping},
 * which can then provide defaults for each mode and an overall default as well.
 *  
 * @author Rainer Schmitz
 * @author Yujin Kim
 * @author John A. Lewis
 */
public class PortletModeParameterHandlerMapping extends AbstractHandlerMapping {

	// lazy init the handlers at startup
	private boolean lazyInitHandlers = false;

    // request parameter name to use for mapping to handlers
    public final static String DEFAULT_PARAMETER_NAME = "action";
	private String parameterName = DEFAULT_PARAMETER_NAME;


	// map passed in from application context file
	private Map portletModeParameterMap;

	// internal map build from passed in map and used by mapping routines
	protected final Map handlerMap = new HashMap();

	// allow duplicate parameter values across portlet modes
	private boolean allowDupParameters = false;

	// map of used parameter values - used for detecting duplicates
	private Map parameterUsedMap = new HashMap();

    /**
     * Get the name of the parameter used for mapping.
     */
    public String getParameterName() {
        return parameterName;
    }
    /**
     * Set the name of the parameter used for mapping.
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

	/**
	 * Set a Map with portlet modes as keys and another map as values.
	 * The sub-map has parameters as keys and handler beans as values.
	 * Convenient for population with bean references.
	 * @param portletModeParameterMap two-level map of portlet modes and parameters to handler beans
	 */
	public void setPortletModeParameterMap(Map portletModeParameterMap) {
		this.portletModeParameterMap = portletModeParameterMap;
	}

	public void initApplicationContext() throws BeansException {

	    // make sure parameterName has a value
		if (this.getParameterName() == null)
            throw new IllegalArgumentException("A parameterName is required");
	
		// make sure the map got initialized
		if (this.portletModeParameterMap == null || this.portletModeParameterMap.isEmpty())
			logger.warn("'portletModeParameterMap' not set on " + this.getClass());
		else {

		    // iterate through the portlet modes in the passed in map
	        for(Iterator itr1 = this.portletModeParameterMap.keySet().iterator(); itr1.hasNext(); ) {
	
	            // get the portlet mode for this map
	            String modeKey = (String)itr1.next();
				PortletMode mode = new PortletMode(modeKey);
	
				// get the map of parameters to handler for this mode
				Object parameterMapObj = this.portletModeParameterMap.get(modeKey);
				if (parameterMapObj != null && !(parameterMapObj instanceof Map))
		            throw new IllegalArgumentException("The value for the portlet mode must be a Map of parameters to handlers");
				Map parameterMap = (Map)parameterMapObj;
				
				// iterate through each parameter value and register the handler
		        for(Iterator itr2 = parameterMap.keySet().iterator(); itr2.hasNext(); ) {
		            String parameter = (String)itr2.next();
					Object handler = parameterMap.get(parameter);
					registerHandler(mode, parameter, handler);
		        }
	        }
		}
	}
	
	/**
	 * Set whether to lazily initialize handlers. Only applicable to
	 * singleton handlers, as prototypes are always lazily initialized.
	 * Default is false, as eager initialization allows for more efficiency
	 * through referencing the handler objects directly.
	 * <p>If you want to allow your handlers to be lazily initialized,
	 * make them "lazy-init" and set this flag to true. Just making them
	 * "lazy-init" will not work, as they are initialized through the
	 * references from the handler mapping in this case.
	 */
	public void setLazyInitHandlers(boolean lazyInitHandlers) {
		this.lazyInitHandlers = lazyInitHandlers;
	}

    /**
     * Set whether to allow duplicate parameter values across different 
     * portlet modes.  Doing this is dangerous because the portlet mode 
     * can be changed by the portal itself and the only way to see that 
     * is a rerender of the portlet.  If the same parameter value is
     * legal in multiple modes, then a change in mode could result in
     * a matched mapping that is not intended and the user could end up
     * in a strange place in the application.
     */
    public void setAllowDupParameters(boolean allowDupParameters) {
        this.allowDupParameters = allowDupParameters;
    }

    /**
	 * Register the given handler instance for the given PortletMode 
	 * and parameter value.
	 * @param mode the PortletMode for which this mapping is valid
	 * @param parameter the parameter value to which this handler is mapped
	 * @param handler the handler instance bean
	 * @throws BeansException if the handler couldn't be registered
	 */
	protected void registerHandler(PortletMode mode, String parameter, 
	        Object handler) throws BeansException {

	    // get the handler map for this mode
	    Map parameterMap = (Map)this.handlerMap.get(mode);
	    if (parameterMap == null) {
	        parameterMap = new HashMap();
	        this.handlerMap.put(mode, parameterMap);
	    }

	    // check for duplicate mapping in this mode
	    Object mappedHandler = parameterMap.get(parameter);
		if (mappedHandler != null)
			throw new ApplicationContextException("Cannot map handler [" + handler + "] to parameter [" + parameter +
			        "] in mode [" + mode + "]: there's already handler [" + mappedHandler + "] mapped");

	    // checkfor duplicate parameter values across all portlet modes
		if (this.parameterUsedMap.get(parameter) != null) {
		    if (this.allowDupParameters)
		        logger.info("Duplicate entries for parameter [" + parameter + "] in different modes");
		    else
		        throw new ApplicationContextException("Duplicate entries for parameter ["+parameter+"] in different modes");
		} else {
		    parameterUsedMap.put(parameter, new Boolean(true));
		}

		// eagerly resolve handler if referencing singleton via name
		if (!this.lazyInitHandlers && handler instanceof String) {
			String handlerName = (String) handler;
			if (getApplicationContext().isSingleton(handlerName)) {
				handler = getApplicationContext().getBean(handlerName);
			}
		}

		// add the parameter value and handler to the map
		parameterMap.put(parameter, handler);
		logger.info("Mapped mode [" + mode + "] parameter [" + parameter + "] onto handler [" + handler + "]");
	}

	/**
	 * Look up a handler for the portlet mode and parameter value of the given request.
	 * @param request current portlet request
	 * @return the looked up handler instance, or null
	 */
	protected Object getHandlerInternal(PortletRequest request) throws Exception {

	    // get the portlet mode
		PortletMode mode = request.getPortletMode();

		// look up the handler map for the given mode
	    Map parameterMap = (Map)this.handlerMap.get(mode);
	    if (parameterMap == null) {
		    if (logger.isDebugEnabled())
				logger.debug("no handler map present for mode [" + mode + "]");
		    return null;
	    }
	    
	    // get the parameter value
	    String parameter = request.getParameter(this.getParameterName());

	    // look up the handler for the given parameter value
	    Object handler = parameterMap.get(parameter);
	    if (logger.isDebugEnabled())
			logger.debug("mode [" + mode + "] parameter [" + parameter + "] = " + 
					     "handler [" + handler + "]");

	    // return the handler
	    return handler;
	}

}