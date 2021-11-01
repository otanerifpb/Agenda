package aplicacaoConsole;
import java.util.ArrayList;

import fachada.Fachada;
import modelo.Participante;
import modelo.Reuniao;

public class AplicacaoConsole {

	public AplicacaoConsole() {
		try {
			Participante participante;				
			Reuniao reuniao;		
			Fachada.inicializar();

			participante = Fachada.criarParticipante("carlos", "carlos@gmail.com");
			System.out.println("participante criado:"+participante);

			ArrayList<String> nomes = new ArrayList<>();
			nomes.add("joao");
			nomes.add("carlos");
			reuniao = Fachada.criarReuniao("20/10/2021 08:00", "teste", nomes);
			System.out.println("reuniao criada:"+reuniao);


			System.out.println("\n---------listagem de participantes-----");
			for(Participante p : Fachada.listarParticipantes()) 
				System.out.println(p);
			System.out.println("\n---------listagem de reunioes");
			for(Reuniao r : Fachada.listarReunioes()) 
				System.out.println(r);

			Fachada.adicionarParticipanteReuniao("jose", 2); 
			Fachada.removerParticipanteReuniao("paulo", 3); // ***n�o volta pois sai no ponto anterior***
			Fachada.removerParticipanteReuniao("maria", 3);		//reuniao 3 sera cancelada
			Fachada.cancelarReuniao(6);
			
			System.out.println("\n---------listagem de participantes-----");
			for(Participante p : Fachada.listarParticipantes()) 
				System.out.println(p);
			System.out.println("\n---------listagem de reunioes");
			for(Reuniao r : Fachada.listarReunioes()) { 
				System.out.println(r);
			System.out.println(r.getDatahora());}

			Fachada.finalizar();
			
		} catch (Exception e) {
			System.out.println("--->"+e.getMessage());
		}		

		//****************
		testarExcecoes();
		//****************

	}


	public static void testarExcecoes() 
	{
		System.out.println("\n-------TESTE EXCE��ES LAN�ADAS--------");
		try 
		{
			Participante p = Fachada.criarParticipante("carlos", "carlos@gmail.com");
			System.out.println("*************1--->Nao lan�ou exce��o para: criar participante "); 
		}catch (Exception e) {System.out.println("1ok--->"+e.getMessage());}

		try 
		{
			ArrayList<String> nomes = new ArrayList<>();
			nomes.add("joao");
			nomes.add("carlos");
			Reuniao r = Fachada.criarReuniao("20/10/2021 08:00", "teste", nomes);
			System.out.println("*************2--->Nao lan�ou exce��o para: criar reuniao"); 
		}catch (Exception e) {System.out.println("2ok--->"+e.getMessage());}

		try 
		{
			ArrayList<String> nomes = new ArrayList<>();
			nomes.add("carlos");
			Reuniao r = Fachada.criarReuniao("20/10/2021 08:00", "teste", nomes);
			System.out.println("*************3--->Nao lan�ou exce��o: criar reuniao com 1 participante"); 
		}catch (Exception e) {System.out.println("3ok--->"+e.getMessage());}

		try 
		{
			Fachada.removerParticipanteReuniao("ze", 1);	
			System.out.println("*************4--->Nao lan�ou exce��o: remover participante inexistente"); 
		}catch (Exception e) {System.out.println("4ok--->"+e.getMessage());}

		try 
		{
			Fachada.cancelarReuniao(999);	
			System.out.println("*************5--->Nao lan�ou exce��o: cancelar reuniao inexistente"); 
		}catch (Exception e) {System.out.println("5ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.adicionarParticipanteReuniao("renato", 4);	
			System.out.println("*************6--->Nao lan�ou exce��o: adicionar participante inexistenteo"); 
		}catch (Exception e) {System.out.println("6ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.adicionarParticipanteReuniao("ana", 14);	
			System.out.println("*************7--->Nao lan�ou exce��o: adicionar participante em reuni�o inexistente"); 
		}catch (Exception e) {System.out.println("7ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.adicionarParticipanteReuniao("ana", 5);	
			System.out.println("*************8--->Nao lan�ou exce��o: adicionar participante na reuni�o"); 
		}catch (Exception e) {System.out.println("8ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.adicionarParticipanteReuniao("ana", 7);	
			System.out.println("*************9--->Nao lan�ou exce��o: adicionar participante na reuni�o"); 
		}catch (Exception e) {System.out.println("9ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.removerParticipanteReuniao("glauco", 5);	
			System.out.println("*************10--->Nao lan�ou exce��o: remover participante inexistente"); 
		}catch (Exception e) {System.out.println("10ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.removerParticipanteReuniao("ana", 15);	
			System.out.println("*************11--->Nao lan�ou exce��o: remover participante da reuni�o inexistente"); 
		}catch (Exception e) {System.out.println("11ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.removerParticipanteReuniao("jose", 1);	
			System.out.println("*************12--->Nao lan�ou exce��o: remover participante da reuni�o"); 
		}catch (Exception e) {System.out.println("12ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.removerParticipanteReuniao("maria", 4);	
			System.out.println("*************13--->Nao lan�ou exce��o: cancelar reuni�o por falta de quorum m�nimo de 2 participantes"); 
		}catch (Exception e) {System.out.println("13ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.cancelarReuniao(15);	
			System.out.println("*************14--->Nao lan�ou exce��o: cancelar reuni�o inexistente"); 
		}catch (Exception e) {System.out.println("14ok--->"+e.getMessage());}
		
		try 
		{
			Fachada.cancelarReuniao(2);	
			System.out.println("*************15--->Nao lan�ou exce��o: que a reuni�o foi cancelada"); 
		}catch (Exception e) {System.out.println("15ok--->"+e.getMessage());}
		

		System.out.println("\n---------listagem de reunioes");
		for(Reuniao r : Fachada.listarReunioes()) 
			System.out.println(r);
		System.out.println("\n---------listagem de participantes-----");
		for(Participante p : Fachada.listarParticipantes()) 
			System.out.println(p);
		
		
		

	}
	


	public static void main (String[] args) 
	{
		AplicacaoConsole aplicacaoConsole = new AplicacaoConsole();
	}
}


