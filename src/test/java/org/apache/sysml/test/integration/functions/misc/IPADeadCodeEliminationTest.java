/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysml.test.integration.functions.misc;

import org.junit.Test;

import org.apache.sysml.hops.OptimizerUtils;
import org.apache.sysml.test.integration.AutomatedTestBase;
import org.apache.sysml.test.integration.TestConfiguration;
import org.apache.sysml.test.utils.TestUtils;

public class IPADeadCodeEliminationTest extends AutomatedTestBase 
{
	private final static String TEST_NAME1 = "IPADeadCodeRemoval_Main";
	private final static String TEST_NAME2 = "IPADeadCodeRemoval_Fun";
	private final static String TEST_NAME3 = "IPADeadCodeRemoval_Fun2";
	private final static String TEST_NAME4 = "IPADeadCodeRemoval_Fun3"; //w/ print
	
	private final static String TEST_DIR = "functions/misc/";
	private final static String TEST_CLASS_DIR = TEST_DIR + IPADeadCodeEliminationTest.class.getSimpleName() + "/";
	
	@Override
	public void setUp() {
		TestUtils.clearAssertionInformation();
		addTestConfiguration( TEST_NAME1, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME1, new String[] { "R" }) );
		addTestConfiguration( TEST_NAME2, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME2, new String[] { "R" }) );
		addTestConfiguration( TEST_NAME3, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME3, new String[] { "R" }) );
		addTestConfiguration( TEST_NAME4, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME4, new String[] { "R" }) );
	}

	@Test
	public void testDeadCodeRemovalMainNoIPA() {
		runIPALiteralReplacementTest( TEST_NAME1, false );
	}
	
	@Test
	public void testDeadCodeRemovalMainIPA() {
		runIPALiteralReplacementTest( TEST_NAME1, true );
	}
	
	@Test
	public void testDeadCodeRemovalFunNoIPA() {
		runIPALiteralReplacementTest( TEST_NAME2, false );
	}
	
	@Test
	public void testDeadCodeRemovalFunIPA() {
		runIPALiteralReplacementTest( TEST_NAME2, true );
	}
	
	@Test
	public void testDeadCodeRemovalFun2NoIPA() {
		runIPALiteralReplacementTest( TEST_NAME3, false );
	}
	
	@Test
	public void testDeadCodeRemovalFun2IPA() {
		runIPALiteralReplacementTest( TEST_NAME3, true );
	}
	
	@Test
	public void testDeadCodeRemovalFun3NoIPA() {
		runIPALiteralReplacementTest( TEST_NAME4, false );
	}
	
	@Test
	public void testDeadCodeRemovalFun3IPA() {
		runIPALiteralReplacementTest( TEST_NAME4, true );
	}

	private void runIPALiteralReplacementTest( String testname, boolean IPA )
	{
		boolean oldFlagIPA = OptimizerUtils.ALLOW_INTER_PROCEDURAL_ANALYSIS;
		if(shouldSkipTest())
			return;
		
		try {
			TestConfiguration config = getTestConfiguration(testname);
			loadTestConfiguration(config);
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + testname + ".dml";
			programArgs = new String[]{"-stats"};
			OptimizerUtils.ALLOW_INTER_PROCEDURAL_ANALYSIS = IPA;
			runTest(true, false, null, -1);
			
			if( IPA && !testname.equals(TEST_NAME4) ) //check for applied dead code removal
				assertTrue(!heavyHittersContainsString("uak+"));
			if( testname.equals(TEST_NAME4) )
				assertTrue(heavyHittersContainsString("uak+"));
		}
		finally {
			OptimizerUtils.ALLOW_INTER_PROCEDURAL_ANALYSIS = oldFlagIPA;
		}
	}
}
