package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Scanner;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.base.types.Event;
import org.yakindu.base.types.Property;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		//masodikFeladat(s);
		//negyedikFeladat(s);
		utolsoFeladat(s);
		
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	// 2. feladat
	public static void masodikFeladat(Statechart s)
	{
		// Reading model
		TreeIterator<EObject> iterator = s.eAllContents();
		int stateCount = 1;
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
						
				State state = (State) content;
				System.out.println(state.getName());

				if(state.getName().isEmpty())
				{
					System.out.println("Az állapot neve üres. Ajánlott név: State"+stateCount);
				}
				if(state.getOutgoingTransitions().isEmpty())
				{
					System.out.println(state.getName()+" egy csapda állapot");
				}
				else					
				{
					for( Transition t : state.getOutgoingTransitions())
					{
						State target = (State) t.getTarget(); 
						System.out.println( state.getName() +"->"+ target.getName());
					}					
				}
				stateCount++;
			}
		}
	}
	// 4.3. feladat
	public static void negyedikFeladat(Statechart s)
	{
		for(Scope scope : s.getScopes())
		{
			System.out.println("variables:");
			for(Property p :scope.getVariables())
			{
				System.out.println(p.getName());
			}
			System.out.println("events:");
			for(Event e :scope.getEvents())
			{
				System.out.println(e.getName());
			}
		}
	}
	// utolso feladat
	public static void utolsoFeladat(Statechart s)
	{
		mainGeneration();
		printGeneration(s);
		cycleGeneration(s);
	}
	public static void mainGeneration()
	{
		System.out.println("public class RunStatechart {\r\n" + 
				"	\r\n" + 
				"	public static void main(String[] args) throws IOException {\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		s.runCycle();\r\n" + 
				"		while(true)\r\n" + 
				"		{\r\n" + 
				"			cycle(s);\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"");
	}
	public static void printGeneration(Statechart s)
	{
		System.out.println("\tpublic static void print(IExampleStatemachine s) {");
		for(Scope scope : s.getScopes())
		{
			for(Property p :scope.getVariables())
			{
				System.out.print("\t\tSystem.out.println(\"");
				System.out.println(p.getName()+" = \" + s.getSCInterface().get"+firstCharToUpper(p.getName())+"());");
			}
		}
		System.out.println("\t}");
	}
	public static void cycleGeneration(Statechart s)
	{
		System.out.println("\r\n" + 
				"	public static void cycle(ExampleStatemachine s)\r\n" + 
				"	{\r\n" + 
				"		Scanner sc= new Scanner(System.in);\r\n" + 
				"		String str= sc.nextLine();\r\n" + 
				"		print(s);\r\n");
		String elseStr = "";
		for(Scope scope : s.getScopes())
		{
			for(Event e :scope.getEvents())
			{
				System.out.println("\t\t"+elseStr+"if(str.equals(\""+e.getName() +"\")){");
				System.out.println("\t\t\ts.raise"+firstCharToUpper(e.getName()) +"();");
				System.out.println("\t\t\ts.runCycle();");
				System.out.println("\t\t}");	
				elseStr = "else ";				
			}
		}
		System.out.println("\t\t"+elseStr+"if(str.equals(\"exit\")){");
		System.out.println("\t\t\tSystem.exit(0);");
		System.out.println("\t\t}");	
		System.out.println("\t}");
		System.out.println("}");
	}
	
	public static String firstCharToUpper(String str)
	{
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}
}
