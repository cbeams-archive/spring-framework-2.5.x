/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.test.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * TestExecutionListeners defines class-level metadata for configuring which
 * {@link TestExecutionListener TestExecutionListeners} should be registered
 * with a {@link TestContextManager}. Typically,
 * {@link TestExecutionListeners @TestExecutionListeners} will be used in
 * conjunction with {@link ContextConfiguration @ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see TestExecutionListener
 * @see TestContextManager
 * @see ContextConfiguration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface TestExecutionListeners {

	/**
	 * <p>
	 * The {@link TestExecutionListener TestExecutionListeners} to register with
	 * a {@link TestContextManager}.
	 * </p>
	 *
	 * @see DependencyInjectionTestExecutionListener
	 * @see DirtiesContextTestExecutionListener
	 * @see TransactionalTestExecutionListener
	 */
	Class<? extends TestExecutionListener>[] value() default { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class };

	/**
	 * <p>
	 * Whether or not {@link #value() TestExecutionListeners} from superclasses
	 * should be <em>inherited</em>.
	 * </p>
	 * <p>
	 * The default value is <code>true</code>, which means that an annotated
	 * class will <em>inherit</em> the listeners defined by an annotated
	 * superclass. Specifically, the listeners for an annotated class will be
	 * appended to the list of listeners defined by an annotated superclass.
	 * Thus, subclasses have the option of <em>extending</em> the list of
	 * listeners. In the following example, <code>AbstractBaseTest</code> will
	 * be configured with <code>DependencyInjectionTestExecutionListener</code>
	 * and <code>DirtiesContextTestExecutionListener</code>; whereas,
	 * <code>TransactionalTest</code> will be configured with
	 * <code>DependencyInjectionTestExecutionListener</code>,
	 * <code>DirtiesContextTestExecutionListener</code>, <strong>and</strong>
	 * <code>TransactionalTestExecutionListener</code>, in that order.
	 * </p>
	 *
	 * <pre class="code">
	 * {@link TestExecutionListeners @TestExecutionListeners}({ DependencyInjectionTestExecutionListener.class,
	 *     DirtiesContextTestExecutionListener.class })
	 * public abstract class AbstractBaseTest {
	 *     // ...
	 * }
	 *
	 * {@link TestExecutionListeners @TestExecutionListeners}({ TransactionalTestExecutionListener.class })
	 * public class TransactionalTest extends BaseTest {
	 *     // ...
	 * }
	 * </pre>
	 *
	 * <p>
	 * If <code>inheritListeners</code> is set to <code>false</code>, the
	 * listeners for the annotated class will <em>shadow</em> and effectively
	 * replace any listeners defined by a superclass.
	 * </p>
	 */
	boolean inheritListeners() default true;

}