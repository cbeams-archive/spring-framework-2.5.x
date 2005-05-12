/*
 * Copyright 2002-2005 the original author or authors.
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
package org.springframework.web.flow.support;

import java.util.Map;

import org.springframework.web.flow.FlowContext;
import org.springframework.web.flow.FlowSession;
import org.springframework.web.flow.RequestContext;
import org.springframework.web.flow.State;
import org.springframework.web.flow.execution.EnterStateVetoException;
import org.springframework.web.flow.execution.FlowExecutionListener;

/**
 * An abstract adapter class for listeners (observers) of flow execution
 * lifecycle events. The methods in this class are empty. This class exists as
 * convenience for creating listener objects; subclass it and override what you
 * need.
 * 
 * @author Erwin Vervaet
 * @author Keith Donald
 */
public abstract class FlowExecutionListenerAdapter implements FlowExecutionListener {

	public void requestSubmitted(RequestContext context) {
	}

	public void starting(RequestContext context, State startState, Map input) throws EnterStateVetoException {
	}

	public void started(RequestContext context) {
	}

	public void requestProcessed(RequestContext context) {
	}

	public void eventSignaled(RequestContext context) {
	}

	public void stateEntering(RequestContext context, State nextState) throws EnterStateVetoException {
	}

	public void stateEntered(RequestContext context, State previousState, State newState) {
	}

	public void paused(RequestContext context) {
		
	}

	public void resumed(RequestContext context) {
		
	}

	public void ended(RequestContext context, FlowSession endedRootFlowSession) {
		
	}

	public void expired(FlowContext flowContext) {
		
	}
}