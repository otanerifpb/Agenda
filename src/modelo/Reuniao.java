package modelo;

import java.util.ArrayList;
import java.time.LocalDateTime;


/**********************************
 * IFPB - Curso Superior de Tec. em Sist. para Internet
 * Programação Orientada a Objetos
 * Prof. Fausto Maranhão Ayres
 **********************************/

public class Reuniao 
{
	private int id; 
	private LocalDateTime datahora;
	private String assunto;
	private ArrayList <Participante> participantes = new ArrayList <Participante>();
	
	public Reuniao(int id, LocalDateTime datahora, String assunto, ArrayList <Participante> participantes) 
	{
		this.id = id;
		this.datahora = datahora;
		this.assunto = assunto;
		this.participantes = participantes; 
	}
	
	public Reuniao(int id, LocalDateTime datahora, String assunto) 
	{
		this.id = id;
		this.datahora = datahora;
		this.assunto = assunto;
	}
	
	public void adicionar(Participante p)
	{
		participantes.add(p);
		p.getReunioes().add(this);
	}
	
	public void remover(Participante p)
	{
		participantes.remove(p);
		p.getReunioes().remove(this);
	}
	
	public Participante localizarParticipante(String nome)
	{
		for(Participante p: participantes)
		{
			if(p.getNome().equals(nome))
				return p;
		}
		return null;
	}
	
	public ArrayList<Participante> getParticipantes() 
	{
		return participantes;
	}
	
	public void setParticipantes(ArrayList<Participante> participantes) 
	{
		this.participantes = participantes;
	}
	
	public int getTotalParticipantes()
	{
		return participantes.size();
	}

	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public LocalDateTime getDatahora() 
	{
		return datahora;
	}
	
	public void setDatahora(LocalDateTime datahora) 
	{
		this.datahora = datahora;
	}
	
	public String getAssunto() 
	{
		return assunto;
	}
	
	public void setAssunto(String assunto) 
	{
		this.assunto = assunto;
	}
	
	@Override
	public String toString() 
	{
		String texto = "id: " + id + ", Horário: " + datahora + ", Assunto: " + assunto;
		texto +=  ", Participantes:";
		if (participantes.isEmpty())
			texto += "Reunião sem participantes";
		else 	
			for(Participante p: participantes) 
				texto += " " + p.getNome();

		return texto ;
	}
}





