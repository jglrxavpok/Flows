import org.jglr.flows.looping.LoopingInputStream;
import org.jglr.flows.looping.StreamLoop;
import org.jglr.flows.looping.defaults.DoStreamLoop;
import org.jglr.flows.looping.defaults.RepeatStreamLoop;
import org.jglr.flows.looping.defaults.WhileStreamLoop;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.function.Supplier;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoopingTests {

    private HashMap<String, Supplier<InputStream>> suppliers;

    @Before
    public void initSuppliers() {
        suppliers = new HashMap<>();
        suppliers.put("classpath", () -> getClass().getResourceAsStream("/test.txt"));
        suppliers.put("file", () -> {
            try {
                return new FileInputStream(new File(".", "build.gradle"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        });
        suppliers.put("http", () -> {
            try {
                return new URL("http://google.com").openStream();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Test
    public void instantiationTests() throws IOException {
        for(String name : suppliers.keySet()) {
            testWithBuffering(suppliers.get(name).get(), name);
            testWithNoBuffering(suppliers.get(name).get(), name);
        }
    }

    private void testWithBuffering(InputStream in, String name) {
        test(in, name, true);
    }

    private void testWithNoBuffering(InputStream in, String name) {
        test(in, name, false);
    }

    private void test(InputStream in, String name, boolean buffering) {
        try {
            new LoopingInputStream(in, buffering);
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
            assertTrue(name+" input streams do not support looping (buffering was set to "+buffering+")", false);
        }
    }

    @Test
    public void testDefaultLoops() throws IOException {
        for(String name : suppliers.keySet()) {
            Supplier<InputStream> sup = suppliers.get(name);
            testLoops(true, sup, name);
            testLoops(false, sup, name);
        }
    }

    private void testLoops(boolean buffering, Supplier<InputStream> in, String name) throws IOException {
        // Test do loop
        testLoop(buffering, in.get(), name, new DoStreamLoop(0, Long.MAX_VALUE));
        testLoop(buffering, in.get(), name, new RepeatStreamLoop(0, Long.MAX_VALUE, 5));

        testLoop(buffering, in.get(), name, new WhileStreamLoop(0, Long.MAX_VALUE, c -> c < 20));
    }

    private void testLoop(boolean buffering, InputStream in, String name, StreamLoop loop) throws IOException {
        LoopingInputStream input = new LoopingInputStream(in, buffering);
        input.setCurrentLoop(loop);
        String filename = name;
        if(buffering) {
            filename += "_buffering";
        }
        filename += "_"+loop.getClass().getSimpleName();
        FileOutputStream out = new FileOutputStream(new File("./tests", filename+".txt"));
        byte[] buffer = new byte[1024*8];
        int i;
        System.out.println(">> Start of "+filename);
        while((i = input.read(buffer)) != -1) {
            out.write(buffer, 0, i);
            System.out.println(">> i="+i+" ("+filename+")");
        }
        System.out.println(">> end of "+filename);
        out.flush();
        out.close();
    }
}
