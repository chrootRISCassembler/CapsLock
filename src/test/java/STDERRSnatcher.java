import java.io.*;

final class STDERRSnatcher extends PrintStream {
    private BufferedReader buffer = new BufferedReader(new StringReader(""));

    STDERRSnatcher(){
        super(new ByteArrayOutputStream());
    }

    final String readLine() {
        try {
            final String line = buffer.readLine();
            if (line == null) {
                buffer = new BufferedReader(new StringReader(out.toString()));
                ((ByteArrayOutputStream)out).reset();
                return buffer.readLine();
            } else {
                return line;
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            return "";
        }
    }
}
