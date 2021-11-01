package repositorio;

/**********************************
 * IFPB - Curso Superior de Tec. em Sist. para Internet
 * Programação Orientada a Objetos
 * Prof. Fausto Maranhão Ayres
 **********************************/
import java.util.ArrayList;
import java.util.TreeMap;

import modelo.Participante;
import modelo.Reuniao;


public class Repositorio 
{
	private TreeMap<String,Participante> participantes = new TreeMap<>();
	private ArrayList<Reuniao> reunioes = new ArrayList<>(); 
	
	public void adicionar(Participante p)
	{
		participantes.put(p.getNome(), p);
	}
	
	public void remover(String p)
	{
		participantes.remove(p);
	}
	
	public Participante localizarParticipante(String nome)
	{
		return participantes.get(nome);
	}

	public void adicionar(Reuniao r)
	{
		reunioes.add(r);
	}
	public void remover(Reuniao r)
	{
		reunioes.remove(r);
	}
	public Reuniao localizarReuniao(int id)
	{
		for(Reuniao r : reunioes)
		{
			if(r.getId()==id)
				return r;
		}
		return null;
	}
	
	public ArrayList<Participante> getParticipantes() 
	{
		return new ArrayList<Participante> (participantes.values());
	}
	public ArrayList<Reuniao> getReunioes() 
	{
		return reunioes;
	}
	
	public int getTotalParticipante()
	{
		return participantes.size();
	}
	
	public int getTotalReunioes()
	{
		return reunioes.size();
	}
	
	public void apagarParticipantes() 
	{
		participantes.clear();
	}
	public void apagarReunioes() 
	{
		reunioes.clear();
	}
	
	public ArrayList<Reuniao> getReunioesSemParticipantes()
	{			
		ArrayList<Reuniao> aux = new ArrayList<Reuniao>();
		for(Reuniao r: reunioes)
			if(r.getParticipantes()==null)
				aux.add(r);

		return aux;
	}
		
	public ArrayList<Reuniao> getReunioesComNParticipantes(int n)
	{			
		ArrayList<Reuniao> aux = new  ArrayList<Reuniao>();
		
		for(Reuniao r: reunioes)
			if(r.getTotalParticipantes()==n)
				aux.add(r);

		return aux;
	}
	
	public  ArrayList<Participante>  getParticipantesSemReunioes()
	{		
		ArrayList<Participante> aux = new ArrayList<Participante>();
		for(Participante p: participantes.values())
			if(p.getTotalReunioes()==0)
				aux.add(p);

		return aux;
	}
	
	//...demais metodos
	
}

