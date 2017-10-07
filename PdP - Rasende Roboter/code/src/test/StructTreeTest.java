package test;

import org.junit.Test;

import solver.StructTree;
import junit.framework.TestCase;

public class StructTreeTest extends TestCase{
	
	@Test
	public void testBuildStack()
	{
		StructTree tree = new StructTree();
		String root = "4;0;Blue&4;9;Green&6;4;Red&13;15;Yellow&";
		String step = "4;0;Blue&4;1;Green&6;4;Red&13;15;Yellow&";
		String leaf = "4;0;Blue&5;1;Green&6;4;Red&13;15;Yellow&";
		
		String intendedSolution = "Move the Green robot in the Up direction.\n";
		
		tree.addPossibility(root, 0);
		
		tree.addPossibility(step, 1);
		tree.addParent(step, root);
		
		tree.addParent(leaf, step);
		
		tree.buildStack(leaf, root);
		
		assertEquals(intendedSolution, tree.getSolution());
	}
}
