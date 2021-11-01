package fachada;
/**********************************
 * IFPB - Curso Superior de Tec. em Sist. para Internet
 * Programação Orientada a Objetos
 * Prof. Fausto Maranhão Ayres
 * Grupo: Glauco Simões & Renato Silva
 * Setembro 2021
 **********************************/

import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport; // send comentado a pedido do professor para fazer o teste.
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import modelo.Participante;
import modelo.Reuniao;
import repositorio.Repositorio;

public class Fachada 
{
	
	public static ArrayList<Participante> part = new ArrayList<>();  
	private static Repositorio repositorio = new Repositorio();	
	private static int idreuniao = 6;

	public static ArrayList<Participante> listarParticipantes() 
	{
		return repositorio.getParticipantes();
	}
	public static ArrayList<Reuniao> listarReunioes() 
	{
		return repositorio.getReunioes();
	}

	public static Participante criarParticipante(String nome, String email) throws Exception 
	{
		nome = nome.trim();
		email = email.trim();
		
		//Verificar se o participande existe
		Participante p = repositorio.localizarParticipante(nome);
		if (p!=null)
			throw new Exception("Participante " + nome + " ja cadastrado(a)");
		
		//Cadastrar participante na reunião
		p = new Participante (nome, email);
		
		//Cadastrar participante no repositório
		repositorio.adicionar(p);
		return p;	
	}	
	
	public static Reuniao criarReuniao (String datahora, String assunto, ArrayList<String> nomes) throws Exception
	{
		assunto = assunto.trim();
		idreuniao++;
		
		DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		LocalDateTime d = LocalDateTime.parse(datahora, parser); 
		
		//Verificar o tamanho da lista de participantes se é > 2
		if (nomes.size()<2) 
		{
			throw new Exception ("Reunião sem quórum mínimo de dois participantes");
		}
		
		//Verificar se o participante existe
		ArrayList<Participante> part = new ArrayList<>();
		for(String n : nomes) 
		{ 
			Participante p = repositorio.localizarParticipante(n);
			
			if(p == null) 
			{
				throw new Exception ("Participante " + n + " inexistente"); 
			}
			else 
			{
				part.add(p);
			}
		}
		
		Reuniao r = new Reuniao(idreuniao, d, assunto);	
		
		//Verificar se o participante já está em outra reunião no mesmo horário
		for(Participante p : part)
		{
			
			ArrayList<Reuniao> lista = p.getReunioes();
			if (lista != null && !lista.isEmpty())
			{
				for (int i = 0; i < lista.size(); i++) 
				{
					if (lista.get(i).getDatahora().equals(d))
					{
						 throw new Exception("Participante já está em outra reunião nesse horário");
					}
					LocalDateTime hinicio = lista.get(i).getDatahora();
					
					Duration duracao = Duration.between(hinicio, d); //(d - hinicio)
					
					long diferenca;
					diferenca = duracao.toHours();
					
					if(Math.abs(diferenca) < 2) 
					{
						throw new Exception("Participante já está em outra reunião nesse horário");
					}
				}
			}
			
			//Adicionar participantes na reunião
			r.adicionar(p);
		}
		
		//Adicionar reunião no repositório
		repositorio.adicionar(r);
		enviarEmail(assunto, "Você foi agendado para a reunião "+assunto);
		return r;
	}

	public static void 	adicionarParticipanteReuniao(String nome, int id) throws Exception 
	{
		nome = nome.trim();
		
		//Verificar se o participante existe
		Participante p = repositorio.localizarParticipante(nome);
		if(p == null) 
		{
			throw new Exception("Participante " + nome + " não consta no cadastro");
		}
		
		//Verificar de a reuniaão existe no repositório
		Reuniao r = repositorio.localizarReuniao(id);
		if(r == null) 
		{
			throw new Exception("Reuniao " + id + " não cadastrada");
		}
		
		//Verificar se o participante já está cadastrado na reunião
		if(r.localizarParticipante(nome) == p) 
		{
			throw new Exception("Participante " + nome + " já cadastrado na reunião " + id);
		}
		
		//Adicionar o participante na reunião 
		r.adicionar(p);
		enviarEmail(r.getAssunto() , "Você foi adicionado na reunião "+r.getAssunto());
	}
		
	public static void 	removerParticipanteReuniao(String nome, int id) throws Exception
	{
		nome = nome.trim();
		
		//Verificar se o participante existe
		Participante p = repositorio.localizarParticipante(nome);
		if(p == null) 
		{
			throw new Exception("Participante " + nome + " não consta no cadastro");
		}
		
		//Verificar se a reunião está cadastrada
		Reuniao r = repositorio.localizarReuniao(id); 
		if(r == null) 
		{
			throw new Exception("Reuniao " + id + " não cadastrada");
		}
		
		//Remover participante da reunião e setar reunião para "null"
		r.remover(p); //bidirecional
		
		//Cancelar reunião por falta de quórum mínimo de 2 participantes
		if (r.getTotalParticipantes() < 2) 
		{
			cancelarReuniao(id);
			enviarEmail(r.getAssunto(), "Você foi removido da reunião "+r.getAssunto());
			throw new Exception("Reunião " + id + " cancelada por falta de quórum mínimo de dois participantes");
		}
		
		enviarEmail(r.getAssunto(), "Você foi removido da reunião");
	}
	
	public static void	cancelarReuniao(int id) throws Exception
	{
		
		//Verificar se a reunião está cadastrada
		Reuniao r = repositorio.localizarReuniao(id);
		if (r == null)
			throw new Exception("Reuniao " + id + " não cadastrada");
	
		//Remover participante da reunião
		for (Participante p : r.getParticipantes()) 
		{
			if(p.getReunioes().contains(r)) 
			{
				p.remover(r);
			}
		}
		
		//Remover reunião do repositório
		repositorio.remover(r);
		enviarEmail(r.getAssunto(), "A reunião "+r.getAssunto()+" foi cancelada");
		throw new Exception("Reunião " + id + " foi cancelada");
	}
	
	public static void inicializar() throws Exception 
	{
		//ler os arquivos textos (formato anexo) os dados dos participantes e 
		//das reuniões e adicioná-los ao repositório

		Scanner arquivo1=null;
		Scanner arquivo2=null;
		//InputStream fonte;
		try
		{
			//fonte = getClass().getResourceAsStream("src/arquivo/participantes.txt");// Esta dando erro
			//arquivo1 = new Scanner(fonte);// Para arquivo interno
			arquivo1 = new Scanner( new File("src/arquivos/participantes.txt"));
		}
		catch(FileNotFoundException e)
		{
			throw new Exception("arquivo de participantes inexistente:");
		}
		
		try
		{
			arquivo2 = new Scanner( new File("src/arquivos/reunioes.txt"));
			
		}
		catch(FileNotFoundException e)
		{
			throw new Exception("arquivo de reunioes inexistente:");
		}

		String linha;	
		String[] partes;	
		String nome, email;
		while(arquivo1.hasNextLine()) 
		{
			linha = arquivo1.nextLine().trim();		
			partes = linha.split(";");	
			nome = partes[0];
			email = partes[1];
			Participante p = new Participante(nome,email);
			repositorio.adicionar(p);
		} 
		arquivo1.close();			

		String id, datahora, assunto;
		String [] nomes;
		while(arquivo2.hasNextLine()) 
		{
			linha = arquivo2.nextLine().trim();		
			partes = linha.split(";");	
			id = partes[0];
			datahora = partes[1];
			assunto = partes[2];
			nomes = partes[3].split(",");	
			ArrayList<Participante> listaparticipantes = new ArrayList<>();
			
			DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			LocalDateTime d = LocalDateTime.parse(datahora, parser); 
			
			for(String n : nomes)
			{
				Participante p = repositorio.localizarParticipante(n);
				listaparticipantes.add(p);
			}
			Reuniao r = new Reuniao(Integer.parseInt(id), d, assunto);
			
			//adicionar em participantes suas reuniões
			for(Participante p : listaparticipantes)
			{
				r.adicionar(p);
			}
			
			repositorio.adicionar(r);
		}
			
		arquivo2.close();	
	}

	public static void	finalizar() throws Exception
	{
		//gravar nos arquivos textos  os dados dos participantes e 
		//das reuniões que estão no repositório
		
		FileWriter arquivo1=null;
		FileWriter arquivo2=null;
		
		try
		{
			arquivo1 = new FileWriter( new File("src/arquivos/participantes.txt") ); 
		}
		catch(IOException e)
		{
			throw new Exception("problema na criação do arquivo de participantes");
		}
		
		try
		{
			arquivo2 = new FileWriter( new File("src/arquivos/reunioes.txt") ); 
		}
		catch(IOException e)
		{
			throw new Exception("problema na criação do arquivo de reunioes");
		}

		for(Participante p : repositorio.getParticipantes()) 
		{
			arquivo1.write(p.getNome() +";" + p.getEmail() +"\n");	
		} 
		arquivo1.close();			

		ArrayList<String> lista;
		String nomes;
		for(Reuniao r : repositorio.getReunioes()) 
		{
			
			String dt = r.getDatahora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
			lista = new ArrayList<>();
			for(Participante p : r.getParticipantes()) 
			{
				lista.add(p.getNome());
			}
			nomes = String.join(",", lista);
			arquivo2.write(r.getId()+";"+dt+";"+r.getAssunto()+";"+nomes+"\n");	
		} 
		arquivo2.close();	

	}
	
	/**************************************************************
	 * 
	 * MÉTODO PARA ENVIAR EMAIL, USANDO UMA CONTA (SMTP) DO GMAIL
	 * ELE ABRE UMA JANELA PARA PEDIR A SENHA DO EMAIL DO EMITENTE
	 * ELE USA A BIBLIOTECA JAVAMAIL 1.6.2
	 * Lembrar de: 
	 * 1. desligar antivirus e de 
	 * 2. ativar opcao "Acesso a App menos seguro" na conta do gmail
	 * 3. se tiver confirmação de 2 etapas, esta deve ser desligada
	 * Conta >> segurança >> verificação 2 etapaca (desabilita)
	 *                    >> Acesso app '-' seguros (ativar)
	 * 
	 **************************************************************/
	public static void enviarEmail(String assunto, String mensagem) 
	{
		try 
		{
			/*
			 * ********************************************************
			 * Obs: lembrar de desligar antivirus e 
			 * de ativar "Acesso a App menos seguro" na conta do gmail
			 * 
			 * pom.xml contem a dependencia javax.mail
			 * 
			 * ********************************************************
			 */
			//configurar emails
			//String emailorigem = "fausto.ayres@gmail.com";
			String emailorigem = "renato.silva.pro@gmail.com";
			//String emailorigem = "glaucorsg@gmail.com";
			String senhaorigem = pegarSenha();
			//String emaildestino = "fausto.ayres@gmail.com";
			String emaildestino = "renato.silva.pro@gmail.com";
			//String emaildestino = "glaucorsg@gmail.com";
			
			//Gmail
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			Session session;
			session = Session.getInstance(props,
					new javax.mail.Authenticator() 
			{
				protected PasswordAuthentication getPasswordAuthentication() 
				{
					return new PasswordAuthentication(emailorigem, senhaorigem);
				}
			});

			MimeMessage message = new MimeMessage(session);
			message.setSubject(assunto);		
			message.setFrom(new InternetAddress(emailorigem));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emaildestino));
			message.setText(mensagem);   // usar "\n" para quebrar linhas
			Transport.send(message);

			System.out.println("e-mail enviado com sucesso");

		} 
		catch (MessagingException e) 
		{
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	/*
	 * JANELA PARA DIGITAR A SENHA DO EMAIL
	 */
	public static String pegarSenha()
	{
		JPasswordField field = new JPasswordField(10);
		field.setEchoChar('*'); 
		JPanel painel = new JPanel();
		painel.add(new JLabel("Entre com a senha do email:"));
		painel.add(field);
		JOptionPane.showMessageDialog(null, painel, "Senha", JOptionPane.PLAIN_MESSAGE);
		String texto = new String(field.getPassword());
		return texto.trim();
	}
}
