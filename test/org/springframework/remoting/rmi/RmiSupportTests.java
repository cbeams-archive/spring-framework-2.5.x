/*
 * Copyright 2002-2004 the original author or authors.
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

package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.rpc.Stub;

import junit.framework.TestCase;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.support.RemoteInvocation;

/**
 * @author Juergen Hoeller
 * @since 16.05.2003
 */
public class RmiSupportTests extends TestCase {

	public void testRmiProxyFactoryBean() throws Exception {
		RmiProxyFactoryBean factory = new RmiProxyFactoryBean() {
			protected Remote lookupStub() {
				return new RemoteBean();
			}
		};
		factory.setServiceInterface(IRemoteBean.class);
		factory.setServiceUrl("rmi://localhost:1090/test");
		factory.afterPropertiesSet();
		assertTrue("Correct singleton value", factory.isSingleton());
		assertTrue(factory.getObject() instanceof IRemoteBean);
		IRemoteBean proxy = (IRemoteBean) factory.getObject();
		proxy.setName("myName");
		assertEquals(RemoteBean.name, "myName");
	}

	public void testRmiProxyFactoryBeanWithRemoteException() throws Exception {
		RmiProxyFactoryBean factory = new RmiProxyFactoryBean() {
			protected Remote lookupStub() {
				return new RemoteBean();
			}
		};
		factory.setServiceInterface(IRemoteBean.class);
		factory.setServiceUrl("rmi://localhost:1090/test");
		factory.afterPropertiesSet();
		assertTrue(factory.getObject() instanceof IRemoteBean);
		IRemoteBean proxy = (IRemoteBean) factory.getObject();
		try {
			proxy.setName("exception");
			fail("Should have thrown RemoteException");
		}
		catch (RemoteException ex) {
			// expected
		}
	}

	public void testRmiProxyFactoryBeanWithBusinessInterface() throws Exception {
		RmiProxyFactoryBean factory = new RmiProxyFactoryBean() {
			protected Remote lookupStub() {
				return new RemoteBean();
			}
		};
		factory.setServiceInterface(IBusinessBean.class);
		factory.setServiceUrl("rmi://localhost:1090/test");
		factory.afterPropertiesSet();
		assertTrue(factory.getObject() instanceof IBusinessBean);
		IBusinessBean proxy = (IBusinessBean) factory.getObject();
		assertFalse(proxy instanceof IRemoteBean);
		proxy.setName("myName");
		assertEquals(RemoteBean.name, "myName");
	}

	public void testRmiProxyFactoryBeanWithBusinessInterfaceAndRemoteException() throws Exception {
		RmiProxyFactoryBean factory = new RmiProxyFactoryBean() {
			protected Remote lookupStub() {
				return new RemoteBean();
			}
		};
		factory.setServiceInterface(IBusinessBean.class);
		factory.setServiceUrl("rmi://localhost:1090/test");
		factory.afterPropertiesSet();
		assertTrue(factory.getObject() instanceof IBusinessBean);
		IBusinessBean proxy = (IBusinessBean) factory.getObject();
		assertFalse(proxy instanceof IRemoteBean);
		try {
			proxy.setName("exception");
			fail("Should have thrown RemoteAccessException");
		}
		catch (RemoteAccessException ex) {
			// expected
		}
	}

	public void testRmiClientInterceptorRequiresUrl() throws Exception{
		RmiClientInterceptor client = new RmiClientInterceptor();
		client.setServiceInterface(IRemoteBean.class);

		try {
			client.afterPropertiesSet();
			fail("url isn't set, expected IllegalArgumentException");
		} 
		catch(IllegalArgumentException e){
			// expected
		}
	}

	public void testRemoteInvocation() throws NoSuchMethodException {
		// let's see if the remote invocation object works:

		RemoteBean rb = new RemoteBean();

		MethodInvocation mi = new ReflectiveMethodInvocation(
				rb, rb, rb.getClass().getDeclaredMethod("setName", new Class[] {String.class}),
		    new Object[] { "bla" }, RemoteBean.class, new ArrayList());

		RemoteInvocation inv = new RemoteInvocation(mi);

		assertEquals("setName", inv.getMethodName());
		assertEquals("bla", inv.getArguments()[0]);
		assertEquals(String.class, inv.getParameterTypes()[0]);

		// this is a bit BS, but we need to test it
		inv = new RemoteInvocation();
		inv.setArguments(new Object[] { "bla" });
		assertEquals("bla", inv.getArguments()[0]);
		inv.setMethodName("setName");
		assertEquals("setName", inv.getMethodName());
		inv.setParameterTypes(new Class[] {String.class});
		assertEquals(String.class, inv.getParameterTypes()[0]);

		inv = new RemoteInvocation("setName", new Class[] {String.class}, new Object[] {"bla"});
		assertEquals("bla", inv.getArguments()[0]);
		assertEquals("setName", inv.getMethodName());
		assertEquals(String.class, inv.getParameterTypes()[0]);
	}

	public void testRmiInvokerWithSpecialLocalMethods() throws Exception {
		String serviceUrl = "rmi://localhost:1090/test";
		RmiProxyFactoryBean factory = new RmiProxyFactoryBean() {
			protected Remote lookupStub() throws Exception {
				return new RmiInvocationHandler() {
					public Object invoke(RemoteInvocation invocation)
							throws RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
						throw new RemoteException();
					}
				};
			}
		};
		factory.setServiceInterface(IBusinessBean.class);
		factory.setServiceUrl(serviceUrl);
		factory.afterPropertiesSet();
		IBusinessBean proxy = (IBusinessBean) factory.getObject();

		// shouldn't go through to remote service
		assertTrue(proxy.toString().indexOf("RMI invoker") != -1);
		assertTrue(proxy.toString().indexOf(serviceUrl) != -1);
		assertEquals(proxy.hashCode(), proxy.hashCode());
		assertTrue(proxy.equals(proxy));

		// should go through
		try {
			proxy.setName("test");
			fail("Should have thrown RemoteAccessException");
		}
		catch (RemoteAccessException ex) {
			// expected
		}
	}


	public static interface IBusinessBean {

		public void setName(String name);

	}


	public static interface IRemoteBean extends Remote {

		public void setName(String name) throws RemoteException;

	}


	public static class RemoteBean implements IRemoteBean, Stub {

		private static String name;
		private static Map properties;

		public RemoteBean() {
			properties = new HashMap();
		}

		public void setName(String nam) throws RemoteException {
			if ("exception".equals(nam)) {
				throw new RemoteException();
			}
			name = nam;
		}

		public void _setProperty(String key, Object o) {
			properties.put(key, o);
		}

		public Object _getProperty(String key) {
			return properties.get(key);
		}

		public Iterator _getPropertyNames() {
			return properties.keySet().iterator();
		}
	}

}
