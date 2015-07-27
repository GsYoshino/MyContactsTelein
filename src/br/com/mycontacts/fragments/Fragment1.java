package br.com.mycontacts.fragments;


import java.text.BreakIterator;
import java.util.List;

import br.com.mycontacts.Formulario;
import br.com.mycontacts.FormularioHelper;
import br.com.mycontacts.MainActivity;
import br.com.mycontacts.R;
import br.com.mycontacts.externals.ListaContatosAdapter;
import br.com.mycontacts.externals.SearchFiltro;
import br.com.mycontacts.lista.dao.ContatoDAO;
import br.com.mycontacts.lista.dao.ligacaoDAO;
import br.com.mycontacts.lista.modelo.Contato;
import br.com.mycontacts.lista.modelo.Ligacao;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

@SuppressLint("NewApi")
public class Fragment1 extends Fragment {
	private ListView lista;
	private Contato contato;
	private ContatoDAO dao;
	private ligacaoDAO ligacaoDao;
	private ligacaoDAO ligacaoDao2;
	
	//Chamada do ContatoDAO � realizada no momento do uso, para conseguir pegar o contexto da classe MainActivity por estarmos em um fragment
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view=inflater.inflate(R.layout.listagem_agenda, null);
		setHasOptionsMenu(true); //Para habilitar ao fragment que crie um novo menu para a tela
		return (view);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
	    //Chamada do searchView para trabalhar com campo de busca
	    SearchView sv = new SearchView(getActivity());
	    sv.setOnQueryTextListener(new SearchFiltro());
	    
	    //Cria item de menu para buscas
	    MenuItem m1 = menu.add(0, 0, 0, "Item 1");
	    //Os Lints das duas linha abaixo podem ser limpados antes de compilar prescionando Ctrl + 1 e clicar: Clear all link Markers
	    m1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    m1.setActionView(sv);
	    
	    //Os itens do menu estao descritos em um XML na pasta menu
 		//INFLATER: especialista em ler o XML e criar itens de menu
 		
 		inflater.inflate(R.menu.lista_agenda, menu); //metodo inflate, devemos passar o xml que queremos inflar
 		super.onCreateOptionsMenu(menu, inflater);
 		 //fragment specific menu creation
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem itemSelect) {
		int itemClicado = itemSelect.getItemId();//descobrir qual o item clicado
		
		switch (itemClicado) {
		case R.id.novo:
			//Responsavel por ir "daqui" at� "formulario.class"
			//Como estamos em um fragment. Acessamos o contexto da classe (this), utilizamos getActivity() 
			Intent irParaFormulario = new Intent(getActivity(), Formulario.class);
            getActivity().startActivity(irParaFormulario); 
			//Uma vez a Intent preparada, basta que a Activity ListaAgenda execute-a. Para isso usamos o startActivity
			break;
		case R.id.caller:
			Intent abrirTecladoLigar = new Intent(Intent.ACTION_DIAL);
			startActivity(abrirTecladoLigar);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(itemSelect);
	}
	
	@Override
	public void onResume() {
    	super.onResume();
    	lista=(ListView) getView().findViewById(R.id.lista);
    	registerForContextMenu(lista);
    	//Esperando o click em um item da lista
        lista.setOnItemClickListener(new OnItemClickListener() {
			//parametros: Adapter da view. View - Cada item da tela. Posicao - posicao da listview. Id - Toda view tem um ID 
        	@Override
			public void onItemClick(final AdapterView<?> adapter, View view, final int posicao, long id) {  		
        		
				final Contato contatoLigar = (Contato) adapter.getItemAtPosition(posicao);

			    //SEMPRE QUE ALTERAR ESSE ARRAY DEVE ALTERAR P FORMULARIOHELPER.
				//String[] operadoras = {"Claro", "Nextel", "Oi", "Vivo", "Tim", "Outro"};
				//String operadora_outro = operadoras[contatoLigar.getOperadora()];
				String operadora_outro = contatoLigar.getOpTelein();
				
				//Toast.makeText(getActivity(), "Minha operadora �: " + MainActivity.operator, Toast.LENGTH_LONG).show();
				
				if (MainActivity.operator != operadora_outro) {

					AlertDialog.Builder caixaDialogo = new AlertDialog.Builder(getActivity());
					caixaDialogo.setTitle("Operadora diferente!");
					caixaDialogo.setMessage("Sua Operadora � "+MainActivity.operator +" e voc� est� ligando para um "+operadora_outro+".\n\nDeseja continuar a liga��o?");
					
					caixaDialogo.setPositiveButton("Sim", new DialogInterface.OnClickListener() {	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						//Toast.makeText(getActivity(), "CLICADO NO YES", Toast.LENGTH_LONG).show();						
						
						Contato registroChamada = (Contato) adapter.getItemAtPosition(posicao);
						/*Intent Impl�cita - Chamar todas as activity do aparelho que sabem fazer discagem
						Para fazer isso precisamos colocar um apelido, no android temos alguns apelidos
						na classe Intent usando o ACTION_CALL
						 */				
						
						Intent irParaTelaDeDiscagem = new Intent(Intent.ACTION_CALL);
						//Precisamos informar p/ que num queremos ligar.
						//Precimos colocar a permiss�o no Manifest para fazer uma liga��o
						Uri discarPara = Uri.parse("tel: " + contatoLigar.getTelefone());
						
						ligacaoDAO daoLigacao = new ligacaoDAO(getActivity()); 
						daoLigacao.salva(registroChamada); //Salvando conte�do
						
						irParaTelaDeDiscagem.setData(discarPara);
						
						startActivity(irParaTelaDeDiscagem);
						}
					});
					
					caixaDialogo.setNegativeButton("N�o", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					
					caixaDialogo.create();
					caixaDialogo.show();					
				}				
			}
		});
        //click longo
        lista.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int posicao, long id) {
				
				contato = (Contato) adapter.getItemAtPosition(posicao);
				
				return false; //Colocamos true para consumir o evento, ou seja, n�o executar o proximo evento que seria o click curto.
			}
        });
    	carregaLista();
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {		
		
		MenuItem contatoSerVisto = menu.add("Ver contato");
		contatoSerVisto.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				indoParaFormulario("contatoMostrar");
				return false;
			}
		});
		
		MenuItem sms=menu.add("Enviar SMS");
		sms.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent irParaSMS = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+contato.getTelefone()));
				startActivity(irParaSMS);
				return false;
			}
		});
		
		MenuItem deletar = menu.add("Deletar");
		deletar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
			    ad.setTitle("Realmente excluir?");
			    ad.setMessage("Deseja realmente excluir o contato selecionado?");
			    /*ad.setButton(getActivity().getString(R.string.hello_world), new DialogInterface.OnClickListener() {
		
			        public void onClick(DialogInterface dialog, int which) {
			        	ContatoDAO dao = new ContatoDAO(getActivity()); //Chamada o SQLITE
			        	dao.deletar(contato);
						dao.close();
			            dialog.dismiss();
			        }
			    });*/
			    ad.setButton("Sim", new DialogInterface.OnClickListener() { 
			    	public void onClick(DialogInterface arg0, int arg1) { 
			    		ContatoDAO dao = new ContatoDAO(getActivity()); //Chamada o SQLITE
			        	dao.deletar(contato);
						dao.close();
						carregaLista();
			    	} 
			    }); 
			    ad.setButton2("N�o", new DialogInterface.OnClickListener() { 
			    	public void onClick(DialogInterface arg0, int arg1) { 
			    	} 
			    });
			    ad.show();
				return false;
			}
		});
		MenuItem alterar = menu.add("Alterar");
		alterar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {				
				indoParaFormulario("contatoAlterar");
				return false;
			}
		});
		
		MenuItem email=menu.add("Enviar e-mail");
		email.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{contato.getEmail()});
				/*i.putExtra(Intent.EXTRA_SUBJECT, "");
				i.putExtra(Intent.EXTRA_TEXT   , "body of email");*/
				try {
					startActivity(Intent.createChooser(i, "Enviar email com: "));
				} catch (Exception e) {
					Toast.makeText(getActivity(), "O contato n�o possui email cadastrado!",Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
		menu.add("Ver no mapa");
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	public void indoParaFormulario(String mostrarOuAlterar){
		Intent irParaFormulario = new Intent(getActivity(), Formulario.class);
		
		//contatoSelecionado � um apelido que sera usado para saber quem � o contato na pr�xima pagina, qndo usarmos o intent.getSerializableExtra
		irParaFormulario.putExtra(mostrarOuAlterar, contato);
		startActivity(irParaFormulario);			
	}
	
	private void carregaLista() {
		ContatoDAO dao = new ContatoDAO(getActivity());
        List<Contato> contatos = dao.getLista();
        dao.close();

        //Aparencia
       // Log.i("LISTA", ""+contatos.get(0).getNome());
        //Transforma as string p/ uma View, quem faz isso � o Adapter
        //Log.i("ADAPTER", ""+contatos.get(0));
        ListaContatosAdapter adapter = new ListaContatosAdapter(contatos,this.getActivity());
        //Colocando as string dos nomes nas linhas da ListView
        lista.setAdapter(adapter);
	}
}
