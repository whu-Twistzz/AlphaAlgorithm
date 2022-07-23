package org.processmining.plugins.gettingstarted.alphaalgorithm;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;


public class AlphaMain {
	//²å¼þ×¢²á
	@Plugin(
			name = "My AlphaAlgorithm", 
			parameterLabels = {"CSVFile"}, 
			returnLabels = { "Petri Net" }, 
			returnTypes = { File.class }, 
			userAccessible = true, 
			help = "Alpha Algorithm'"
		)
	@UITopiaVariant(
			affiliation = "zjy", 
			author = "ZhaoJingyang", 
			email = "zjy@whu.edu.cn"
		)
	public static File alpha(PluginContext context,String route)
	{
		 Set<Trace> eventLog = Utils.readInputFromCSV(route);
	     Alpha.takeInAccountLoopsLengthTwo = true;
	     Alpha.discoverWorkflowNetwork(eventLog);
	     File file = new File("E:\\prompic\\graphOut.png");
	        Desktop desktop=Desktop.getDesktop();
	        try {
	            desktop.open(file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }  		 
	     return file;
	}
    
}
