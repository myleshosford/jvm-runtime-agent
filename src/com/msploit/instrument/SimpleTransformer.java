package com.msploit.instrument;

//Core Java imports
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// ASM bytecode modification import
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class SimpleTransformer implements ClassFileTransformer {
    
    
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private ArrayList<String> blacklist = new ArrayList();
    
    public SimpleTransformer(final ArrayList<String> list) {
        super();
        blacklist = list;
    }


    public byte[] transform(ClassLoader loader, String className, Class redefiningClass, ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
        boolean blacklistbool = false;
        
        for (String blackclass : blacklist) {
            if (className.startsWith(blackclass)) {
                blacklistbool = true;
                break;
            }     else {
                blacklistbool = false;
            }
        }
        
        if (blacklistbool) {
            System.out.println("Blacklisting class: " + className);
            return bytes;
        } else {
            try {
                System.out.println("[+] " + dateFormat.format(new Date()) + " - Loading class: " + className);
                byte[] result = bytes;
                    // Create a reader for the existing bytes.	
                    ClassReader reader = new ClassReader(bytes);
                    // Create a writer
                    ClassWriter writer = new ClassWriter(true);
                    // Create our class adapter, pointing to the class writer
                    // and then tell the reader to notify our visitor of all 
                    // bytecode instructions
                    reader.accept(new PrintStatementClassAdapter(writer, className), true);
                    // get the result from the writer.
                    result = writer.toByteArray();
                    return result;                    
            } catch (Exception e) {
                System.out.println("Exception: " + e);
                return bytes;
            }
        }
    }
	

    private class PrintStatementClassAdapter extends ClassAdapter {
        private String className;
    
        PrintStatementClassAdapter(ClassVisitor visitor, String theClass) {
            super(visitor);
            className = theClass;   
        }
		
        @Override 
        public MethodVisitor visitMethod(int arg0, String name, String descriptor, String signature, String[] exceptions) {
            try {
                return new PrintStatementMethodAdapter(super.visitMethod(arg0, name, descriptor, signature, exceptions), className, name, descriptor);     
            } catch (Exception e) {
                System.out.print("Exception: " + e);
                return null;
            }
           
        }

    }
	
       private class PrintStatementMethodAdapter extends MethodAdapter {
            private String methodName;
            private String methodDescriptor;
            private String className;
		
            PrintStatementMethodAdapter(MethodVisitor visitor, String theClass, String name, String descriptor) {
                super(visitor);
                methodName = name;
                methodDescriptor = descriptor;
                className = theClass;
            }
		
            @Override 
            public void visitCode() {
               try {
                super.visitCode();
                // load the system.out field into the stack
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                
                // load the constant string we want to print into the stack
                // this string is created by the values we get from ASM
                super.visitLdcInsn("[+] " + dateFormat.format(new Date()) + " - Method called: " + className + "." + methodName + "\t Type: " + methodDescriptor);
               
                // trigger the method instruction for 'println'
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");       
               } catch (Exception e) {
                   
                System.out.println("Exception: " + e);   
               }			
            } //end of visitCode()
            
        } //end of inner class
}
