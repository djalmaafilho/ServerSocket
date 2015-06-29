package base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteSocket {

	public static void main(String[] args) {
		Socket kkSocket = null;
		try {
			System.out.println("Tentando conectar");
			kkSocket = new Socket("0.0.0.0", 4444);
			PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(kkSocket.getInputStream()));
			
			while(true){
				while(!in.ready()){
					System.out.println("Aguardando Server...");
					Thread.sleep(200);
				}
				String mensagem = in.readLine();
				
				if(mensagem != null){
					System.out.println(mensagem);
				}
				
				out.println("Olá servidor!!!");
				out.flush();
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(kkSocket != null)
			kkSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}