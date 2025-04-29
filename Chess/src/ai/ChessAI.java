package ai;

import java.io.*;

public class ChessAI {
    private Process stockfish;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChessAI(String enginePath) {
        try {
            // Start the Stockfish process
            stockfish = new ProcessBuilder(enginePath).start();
            reader = new BufferedReader(new InputStreamReader(stockfish.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(stockfish.getOutputStream()));

            // Initialize Stockfish with the "uci" command
            sendCommand("uci");
            String output = readOutput();  // Wait for the "uciok" message from Stockfish
            //System.out.println("Stockfish initialized with output: " + output);  // Debugging: print output from Stockfish

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a command to Stockfish
    public void sendCommand(String command) {
        try {
            System.out.println("Sending command: " + command);  // Debugging: print the sent command
            writer.write(command + "\n");
            writer.flush();  // Flush to ensure the command is sent immediately
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads the output from Stockfish
    public String readOutput() {
        StringBuilder output = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                // Stop reading once we get the "uciok" or "bestmove"
                if (line.startsWith("uciok") || line.startsWith("bestmove")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    // Get the best move from Stockfish
    public String getBestMove(String fen) {
        sendCommand("position fen " + fen); // Set the position in Stockfish
        sendCommand("go depth 15"); // Request best move

        // Read and parse the output to extract the best move
        String output = readOutput();
        //System.out.println("Stockfish output: " + output);  // Print output to debug

        String bestMove = null;
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.startsWith("bestmove")) {
                bestMove = line.split(" ")[1];  // Get the move after "bestmove"
                break;
            }
        }
        return bestMove;
    }

    // Close the Stockfish process
    public void close() {
        sendCommand("quit");  // Send quit command
        try {
            stockfish.destroy();  // Destroy the process
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void stop() {
    // Add logic here to stop the Stockfish process if running
    System.out.println("ChessAI stopped.");
}


    public static void main(String[] args) {
        // Instantiate ChessAI with the path to your Stockfish engine
        ChessAI ai = new ChessAI( "C:/Users/David Ozowara/Documents/NetBeansProjects/Chesst/Chesstt/stockfish-windows-x86-64-avx2.exe");

        // Example FEN for testing
        String fen = "rnbqkb1r/pppppppp/7n/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        // Get the best move from Stockfish
        String bestMove = ai.getBestMove(fen);
        System.out.println("Best move: " + bestMove);

        // Close the ChessAI (Stockfish) process
        ai.close();
    }
    
    
    
}
