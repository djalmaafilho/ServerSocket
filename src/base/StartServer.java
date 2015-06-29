package base;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class StartServer {

	private static ExecutorService pool;
	private static ServerSocket serverSocket;
	private static Robot robot;
	private static AtomicInteger count = new AtomicInteger();
	private static SocketThread cliente;
	public static void main(String[] args) {
		try { 
				pool = Executors.newSingleThreadExecutor();
				robot = new Robot();
				serverSocket = new ServerSocket(4444);
				
				JFrame tela = new JFrame("Mouse Server");
				tela.setSize(300, 100);
				tela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JLabel l = new JLabel(" Endereço IP: "+InetAddress.getLocalHost().getHostAddress());
				tela.add(l);
				tela.setVisible(true);			
				
				while(true){
					Socket clientSocket = serverSocket.accept();
					int qtd = count.get();
					System.out.println("Clientes existentes "+qtd);
					if(qtd >= 1){
						System.out.println("Chamando cancelamento");
						cliente.cancel();
					}
					
					cliente = new SocketThread(clientSocket);
					cliente.start();
				}
		}catch (IOException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	static class SocketThread extends Thread{
		Socket clientSocket;
		
		public SocketThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		@Override
		public void run() {
			super.run();
			System.out.println("Clientes "+count.addAndGet(1));
			try {
				PrintWriter out =
						new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				
				out.println(200);
				out.flush();
				
				while(true){
					while(!clientSocket.isClosed() && !in.ready()){
						Thread.sleep(50);
					}
					
					String mensagem = in.readLine();
					if(mensagem != null && mensagem.contains("%")){
						String[] params = mensagem.split("%");
						robot.mouseMove((int)Float.parseFloat(params[0]),(int)Float.parseFloat(params[1]));
					}else if(mensagem != null && mensagem.equals("btEsqDown")){
						robot.mousePress(java.awt.event.MouseEvent.BUTTON1_DOWN_MASK);
					}else if(mensagem != null && mensagem.equals("btEsqUp")){
						robot.mouseRelease(java.awt.event.MouseEvent.BUTTON1_DOWN_MASK);
					}else if(mensagem != null && mensagem.equals("btDirDown")){
						robot.mousePress(java.awt.event.MouseEvent.BUTTON3_DOWN_MASK);
					}else if(mensagem != null && mensagem.equals("btDirUp")){
						robot.mouseRelease(java.awt.event.MouseEvent.BUTTON3_DOWN_MASK);
					}else{
						System.out.println(mensagem);
					}
					
					out.println(200);
					out.flush();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
                if(clientSocket != null)
                	clientSocket.close();
                	
            } catch (Exception e) {
                e.printStackTrace();
            }
			
			int qtd = count.decrementAndGet();
			System.out.println("Encerrando cliente, restam : "+qtd);
		}
		
		@Override
		public synchronized void start() {
			pool.execute(this);
		}
		
		void cancel(){
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}