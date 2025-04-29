/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ai;

import java.io.IOException;

/**
 *
 * @author David Ozowara
 */

import java.io.*;
public class TestStockfish {
    public static void main(String[] args) {
        // Instantiate ChessAI with the path to your Stockfish engine
        ChessAI ai = new ChessAI( "C:/Users/David Ozowara/Documents/NetBeansProjects/Chesstt/stockfish-windows-x86-64-avx2.exe");
//"C:/Users/David Ozowara/Documents/NetBeansProjects/Chesstt/stockfish-windows-x86-64-avx2.exe"
        // Example FEN for testing
        String fen = "rnbqkb1r/pppppppp/7n/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        // Get the best move from Stockfish
        String bestMove = ai.getBestMove(fen);
        System.out.println("Best move: " + bestMove);

        // Close the ChessAI (Stockfish) process
        ai.close();
    }
}




















//public class TestStockfish {
//    public static void main(String[] args) {
//        try {
//            Process process = new ProcessBuilder(
//                "C:/Users/David Ozowara/Documents/NetBeansProjects/Chesst/Chesstt/stockfish-windows-x86-64-avx2.exe"
//            ).start();
//            
//            System.out.println("Stockfish started!");
//
//            // Create input/output streams
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//
//            // Send UCI initialization
//            writer.write("uci\n");
//            writer.flush();
//            
//            // Read until we get "uciok"
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//                if (line.equals("uciok")) break;
//            }
//
//            // Send a position in FEN format
//            String fen = "rnbqkb1r/pppppppp/7n/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//            writer.write("position fen " + fen + "\n");
//            writer.write("go depth 15\n");
//            writer.flush();
//            
//            System.out.println("Sent position & search command to Stockfish...");
//
//            // Read Stockfish's response and look for "bestmove"
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);  // Debugging output
//                if (line.startsWith("bestmove")) {
//                    System.out.println("Best move: " + line);
//                    break;
//                }
//            }
//
//            // Close resources
//            writer.close();
//            reader.close();
//            process.destroy();
//
//        } catch (IOException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//    }
//}



