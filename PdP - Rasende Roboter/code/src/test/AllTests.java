package test;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BoardTest.class, BoxTest.class, ClientTest.class,
		ControllerTest.class, CountDownTest.class, GameTest.class,
		NetworkTest.class, PlayerManagerTest.class, PlayerTest.class,
		ProtocolTest.class, RobotTest.class, StructTreeTest.class })
public class AllTests extends TestSuite {
}
