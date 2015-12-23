import org.jglr.flows.looping.LoopingInputStream;
import org.jglr.flows.looping.StreamLoop;
import org.jglr.flows.looping.defaults.DoStreamLoop;
import org.jglr.flows.looping.defaults.RepeatStreamLoop;
import org.junit.Test;

import java.io.*;
import java.util.function.Function;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoopingTests {

    @Test
    public void instantiationTests() throws FileNotFoundException {
        testWithBuffering(getClass().getResourceAsStream("/test.txt"), "classpath");
        testWithBuffering(new FileInputStream(new File(".", "build.gradle")), "file");

        testWithNoBuffering(getClass().getResourceAsStream("/test.txt"), "classpath");
        testWithNoBuffering(new FileInputStream(new File(".", "build.gradle")), "file");
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
            ex.printStackTrace();
            assertTrue(name+" input streams do not support looping, buffering was set to "+buffering, false);
        }
    }

    @Test
    public void testDefaultLoops() throws IOException {
        testLoops(true, (a) -> getClass().getResourceAsStream("/test.txt"), "classpath");
        testLoops(false, (a) -> {
            try {
                return new FileInputStream(new File(".", "build.gradle"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }, "file");
    }

    private void testLoops(boolean buffering, Function<Void, InputStream> in, String name) throws IOException {
        // Test do loop
        testLoop(buffering, in.apply(null), name, new DoStreamLoop(0, Long.MAX_VALUE));
        testLoop(buffering, in.apply(null), name, new RepeatStreamLoop(0, Long.MAX_VALUE, 5));
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
        while((i = input.read(buffer)) != -1) {
            out.write(buffer, 0, i);
            System.out.println(">> i="+i+" ("+filename+")");
        }
        System.out.println(">> end of "+filename);
        out.flush();
        out.close();
    }
}
