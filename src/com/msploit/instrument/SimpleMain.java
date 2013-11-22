package com.msploit.instrument;
 
import java.lang.instrument.Instrumentation;
 
public class SimpleMain {
    
    public static void agentmain(String agentArguments, Instrumentation instrumentation) {	
        try {
            AgentForm agentform = new AgentForm();
            agentform.pack();
            agentform.setVisible(true);
            agentform.loadInstrumentor(instrumentation);
            //instrumentation.addTransformer(new SimpleTransformer());
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }	
}